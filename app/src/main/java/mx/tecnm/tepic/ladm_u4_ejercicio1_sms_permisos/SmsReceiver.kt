package mx.tecnm.tepic.ladm_u4_ejercicio1_sms_permisos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast
import java.sql.SQLException

/*
    RECEIVER = evento u oyente de android que permite la laectura de eventos del sistema operativo.
*/

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var extras = intent.extras

        if (extras != null) {
            var sms = extras.get("pdus") as Array<Any>

            for (indice in sms.indices) {
                val formato = extras.getString("format")

                var smsMensaje = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray) //versiones menores a 26 no requerian de formato
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                //GUARDAR SOBRE TABLA SQLITE
                try {
                    var baseDatos = BaseDatos(context,"entrantes",null,1)
                    var insertar = baseDatos.writableDatabase
                    var SQL = "INSERT INTO ENTRANTES VALUES('${celularOrigen}','${contenidoSMS}')"
                    insertar.execSQL(SQL)
                    baseDatos.close()
                }catch (err: SQLiteException){
                    Toast.makeText(context, err.message, Toast.LENGTH_LONG).show()
                }

                Toast.makeText(context, "ENTRO CONTENIDO ${contenidoSMS}", Toast.LENGTH_LONG).show()
            }
        }
    }

}