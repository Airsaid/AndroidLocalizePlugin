package com.airsaid.localization.translate.impl.deeplx;

import com.airsaid.localization.translate.util.GitHubReleaseDownloader;
import org.junit.jupiter.api.Test;

public class DeepLXTest {
    @Test
    public void DeepLXTranslate() {
        final String REPO_URL = "https://api.github.com/repos/OwO-Network/DeepLX/releases/latest";

        GitHubReleaseDownloader.getRelease(REPO_URL);
    }
}
