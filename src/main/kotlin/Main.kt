import com.jerry.rt.RtClient
import java.lang.Thread.sleep

fun main(args: Array<String>) {
    val rtClient = RtClient.connect("192.168.101.4",8080)
    var id = 0
    rtClient.connect("rt/aa",
        {
            println("connect")
        },
        {
            println("msg:${it.getBody()}")
        },{
            println("close")
        }
    )

    while (true){
        sleep(500)
    }
}