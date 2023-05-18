import com.jerry.rt.RtClient
import java.lang.Thread.sleep
import java.util.UUID

//fun main(args: Array<String>) {//todo remove
//    val rtClient = RtClient.connect("192.168.101.4",8080)
//    var id = 0
//    rtClient.connect("/a/b",
//        {
//            println("connect")
//            rtClient.sendMessage("hallo")
//        },
//        {
//            val body = it.getBody()?:""
//            println("msg:${body}")
//            rtClient.sendMessage(UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString())
//        },{
//            println("close")
//        }
//    )
//
//    while (true){
//        sleep(500)
//    }
//}