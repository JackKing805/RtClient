import com.jerry.rt.RtClient
import com.jerry.rt.i.StateListener
import java.lang.Thread.sleep

fun main(args: Array<String>) {
    RtClient.connect("localhost",8080,object :StateListener{

    })

    while (true){
        sleep(500)
    }
}