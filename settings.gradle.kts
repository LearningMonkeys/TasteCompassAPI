pluginManagement {
	repositories {
		mavenCentral()
		maven { url = uri("https://repo.spring.io/release") }
		maven { url = uri("https://repo.spring.io/snapshot") }
		maven { url = uri("https://repo.spring.io/milestone") }
		gradlePluginPortal()
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "tasteCompass"
include("Controller")
include("Application")
include("BaseUtil")
include("Analyzer")
include("Embedding")
include("Data")
include("Search")
