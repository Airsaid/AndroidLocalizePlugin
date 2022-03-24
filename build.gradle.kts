fun properties(key: String) = project.findProperty(key).toString()

plugins {
  // Java support
  id("java")
  // Gradle IntelliJ Plugin
  id("org.jetbrains.intellij") version "1.3.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))

  // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
  plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
  // Set the JVM compatibility versions
  properties("javaVersion").let {
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
      options.encoding = "UTF-8"
    }
  }

  wrapper {
    gradleVersion = properties("gradleVersion")
  }

  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))
  }

  test {
    useJUnitPlatform()
  }

  publishPlugin {
    token.set(System.getenv("INTELLIJ_PUBLISH_TOKEN"))
  }
}

dependencies {
  implementation("com.google.code.gson:gson:2.8.7")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

  implementation("com.alibaba:fastjson:1.2.76")
  implementation("com.squareup.okhttp3:okhttp:4.9.3")
  implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.3"))

  // define any required OkHttp artifacts without version
  implementation("com.squareup.retrofit2:retrofit:2.7.0")
  implementation("com.squareup.okio:okio:1.8.0")


  // Retrofit

}