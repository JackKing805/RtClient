import com.jerry.rt.RtClient
import java.lang.Thread.sleep

fun main(args: Array<String>) {
    val rtClient = RtClient.connect("localhost",8080)
    var id = 0
    rtClient.connect("rt/aa",
        {
            println("connect")
            rtClient.sendMessage("hallo")
        },
        {
            val body = it.getBody()?:""
            println("msg:${body}")
            rtClient.sendMessage(body)
        },{
            println("close")
        }
    )

    while (true){
        sleep(500)
    }
}