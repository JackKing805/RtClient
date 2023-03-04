import com.jerry.rt.RtClient
import java.lang.Thread.sleep

fun main(args: Array<String>) {
    RtClient.connect("localhost",8080).connect()

    while (true){
        sleep(500)
    }
}