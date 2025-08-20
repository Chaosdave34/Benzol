-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.pdfbox.pdmodel.encryption.PublicKeySecurityHandler
-dontwarn org.apache.xmpbox.DateConverter
-dontwarn io.ktor.network.sockets.SocketBase$attachFor$1

-keep class io.ktor.client.engine.cio.** {*;}
-keep class io.ktor.serialization.kotlinx.** {*;}