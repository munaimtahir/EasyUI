plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("junit:junit:4.13.2")
}

application {
    mainClass.set("com.easyui.backend.ServerKt")
}
