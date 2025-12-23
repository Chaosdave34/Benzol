-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.pdfbox.pdmodel.encryption.**
-dontwarn org.apache.xmpbox.DateConverter
-dontwarn io.ktor.network.sockets.SocketBase$attachFor$1
-dontwarn org.apache.pdfbox.io.IOUtils

-keep class io.ktor.client.engine.cio.** {*;}
-keep class io.ktor.serialization.kotlinx.** {*;}
-keep class org.apache.commons.logging.impl.** {*;}

-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keep class * implements org.freedesktop.dbus.** { *; }