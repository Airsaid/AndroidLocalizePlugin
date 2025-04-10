package com.airsaid.localization.translate.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class GitHubReleaseDownloader {
    protected static final Logger LOG = Logger.getInstance(GitHubReleaseDownloader.class);
    private static final OkHttpClient mClient = new OkHttpClient.Builder()
            .proxySelector(ProxySelector.getDefault())
            .build();

    private static final int MAX_RETRIES = 3;          // 最大重试次数
    private static final long BASE_TIMEOUT_SEC = 10;   // 基础超时时间
    private static final double TIMEOUT_BACKOFF = 1.5; // 超时时间指数退避因子

    public static void getRelease(@NotNull String repoUrl) {
        try {
            // 1. 获取最新版本信息
            JsonObject release = getLatestRelease(repoUrl);
            String version = release.get("tag_name").getAsString();
            System.out.println("最新版本: " + version);

            // 2. 获取当前系统信息
            String os = getNormalizedOS();
            String arch = getNormalizedArch();
            System.out.println("检测到系统: " + os + " " + arch);

            // 3. 查找匹配的资产文件
            JsonArray assets = release.getAsJsonArray("assets");
            String downloadUrl = findMatchingAsset(assets, os, arch);
            if (downloadUrl == null) {
                throw new RuntimeException("未找到匹配的下载文件");
            }
            // https://github.com/OwO-Network/DeepLX/releases/download/v1.0.7/deeplx_windows_amd64.exe
            // https://github.moeyy.xyz/
            LOG.info("Download url: " + downloadUrl);
            downloadUrl = "https://github.moeyy.xyz/" + downloadUrl;

            // 4. 下载文件
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                try {
                    attempt++;
                    long currentTimeout = (long) (BASE_TIMEOUT_SEC * Math.pow(TIMEOUT_BACKOFF, attempt - 1));
                    System.out.printf("第 %d 次尝试 (超时: 连接 %ds/读取 %ds)...%n",
                            attempt, currentTimeout, currentTimeout * 2);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .proxySelector(ProxySelector.getDefault())
                            .connectTimeout(currentTimeout, TimeUnit.SECONDS)
                            .readTimeout(currentTimeout * 2, TimeUnit.SECONDS)
                            .build();
                    downloadFile(client, downloadUrl, fileName);
                    client.dispatcher().executorService().shutdown();
                    System.out.println("下载成功！文件保存至: " + fileName);
                    return;
                } catch (IOException e) {
                    handlerError(e, attempt);
                }
            }

        } catch (Exception e) {
            LOG.error("getRelease Error", e);
        }
    }

    private static JsonObject getLatestRelease(@NotNull String repoUrl) throws IOException {
        Request request = new Request.Builder()
                .url(repoUrl)
                .header("User-Agent", "Java/DeepLX-Updater")// GitHub要求User-Agent
                .build();

        try (Response response = mClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed code " + response);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Response body is null");
            }
            return GsonUtil.getInstance().getGson().fromJson(body.charStream(), JsonObject.class);
        }
    }

    private static String getNormalizedOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return "windows";
        if (osName.contains("linux")) return "linux";
        if (osName.contains("mac")) return "darwin";
        return "unknown";
    }

    private static String getNormalizedArch() {
        String osArch = System.getProperty("os.arch").toLowerCase();
        if (osArch.contains("aarch64") || osArch.contains("arm64")) return "arm64";
        if (osArch.contains("amd64") || osArch.contains("x86_64")) return "amd64";
        return "unknown";
    }

    private static String findMatchingAsset(JsonArray assets, String os, String arch) {
        for (int i = 0; i < assets.size(); i++) {
            JsonObject asset = assets.get(i).getAsJsonObject();
            String name = asset.get("name").getAsString().toLowerCase();
            String url = asset.get("browser_download_url").getAsString();

            // 匹配规则示例: deeplx_windows_amd64.exe
            if (name.contains(os) && name.contains(arch)) {
                return url;
            }
        }
        return null;
    }

    private static void downloadFile(OkHttpClient client, String fileUrl, String savePath) throws IOException {
        Request request = new Request.Builder()
                .header("User-Agent", "Java/DeepLX-Updater")
                .url(fileUrl)
                .build();
        try (Response response = client.newCall(request).execute()) {
            validateResponse(response);
            if (!response.isSuccessful()) {
                throw new IOException("Request failed code " + response);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Response body is null");
            }
            try (InputStream in = body.byteStream()) {
                Files.copy(in, Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void handlerError(IOException e, int attempt) {
        String errorType = "Network error";
        if (e.getMessage().contains("timeout")) {
            errorType = e.getMessage().contains("connect") ? "connect timeout" : "read timeout";
        }
        LOG.error("error: " + errorType + "; msg: " + e.getMessage());
        if (attempt < 10) {
            LOG.info("waiting try");
            try {
                Thread.sleep(1000 * (long) Math.pow(2, attempt));
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void validateResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Http code " + response.code() + ": " + response.message());
        }
        String contentLength = response.header("Content-Length");
        if (contentLength == null) {
            LOG.warn("Warning: Server is not return file size");
        } else {
            LOG.info("Content-Length: " + formatFileSize(Long.parseLong(contentLength)));
        }
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        }
        int exp = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), "KMGTPE".charAt(exp - 1));
    }
}
