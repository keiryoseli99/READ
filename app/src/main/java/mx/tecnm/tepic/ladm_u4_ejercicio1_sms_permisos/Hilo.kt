package mx.tecnm.tepic.ladm_u4_ejercicio1_sms_permisos

import android.database.sqlite.SQLiteException
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class Hilo(p: MainActivity): Thread() {
    val puntero = p

    override fun run() {
        super.run()
        while (true){
            puntero.runOnUiThread {
                //puntero.textView.setOnClickListener {
                    try {
                        val cursor = BaseDatos(puntero,"entrantes",null,1)
                            .readableDatabase
                            .rawQuery("SELECT * FROM ENTRANTES", null)

                        var ultimo = ""
                        if (cursor.moveToFirst()){
                            do{
                                ultimo = "ULTIMO MENSAJE RECIBIDO"+
                                        "\nCELULAR ORIGEN: "+cursor.getString(0)+
                                        "\nMENSAJE SMS: "+cursor.getString(1)+
                                        "\n------------------------"
                            }while (cursor.moveToNext())
                        }else{
                            ultimo = "SIN MENSAJES AUN, TABLA VACIA"
                        }
                        puntero.textView.setText(ultimo)
                    }catch (err: SQLiteException){
                        Toast.makeText(puntero, err.message, Toast.LENGTH_LONG).show()
                    }
                //}
            }//puntero
            sleep(2000)
        }//while
    }//run
}//Thread