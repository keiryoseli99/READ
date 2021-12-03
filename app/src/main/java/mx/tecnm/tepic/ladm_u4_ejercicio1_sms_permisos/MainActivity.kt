package mx.tecnm.tepic.ladm_u4_ejercicio1_sms_permisos

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    val siPermiso = 1 //significa que si (puede ser cualquier numero)
    val siPermisoReceiver = 2 //recibir mensajes que van entrando
    val siPermisoLectura = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS), siPermisoReceiver)
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS), siPermisoLectura)
        }else{
            leerSMSEntrada()
        }

        //mayores de la m
        button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
            } else {
                envioSMS()
            }
        }

        //Hacer esto en un hilo para que se actualice automaticamento
        // sin necesidad de precionar el texto para que aparezca
        val hilo = Hilo(this)
        textView.setOnClickListener {
            try {
                hilo.start()
            }catch (io: Exception){
                Toast.makeText(this, "ERROR HILO Y EJECUTADO START", Toast.LENGTH_LONG).show()
            }
        }
        /*textView.setOnClickListener {
            try {
                val cursor = BaseDatos(this,"entrantes",null,1)
                    .readableDatabase
                    .rawQuery("SELECT * FROM ENTRANTES", null)

                var ultimo = ""
                if (cursor.moveToFirst()){
                    do{
                        ultimo = "ULTIMO MENSAJE RECIBIDO\nCELULAR ORIGEN"+cursor.getString(0)+
                                "\nMENSAJE SMS: "+cursor.getString(1)+
                                "\n------------------------"
                    }while (cursor.moveToNext())
                }else{
                    ultimo = "SIN MENSAJES AUN, TABLA VACIA"
                }
                textView.setText(ultimo)
            }catch (err: SQLiteException){
                Toast.makeText(this, err.message, Toast.LENGTH_LONG).show()
            }
        }*/
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermiso){
            envioSMS()
        }
        if (requestCode == siPermisoReceiver) {
            mensajeRecibir()
        }
        if (requestCode == siPermisoLectura) {
            leerSMSEntrada()
        }
    }

    private fun leerSMSEntrada() {
        var cursor = contentResolver.query(Uri.parse("content://sms/"),null,null,null,null)

        var resultado = ""
        if (cursor != null) {
            if(cursor.moveToFirst()){
                var posColumnaCelularOrigen = cursor.getColumnIndex("address")
                var posColumnaMensaje = cursor.getColumnIndex("body")
                var posColumnaFecha = cursor.getColumnIndex("date")
                do {
                    val fechamensaje = cursor.getString(posColumnaFecha)
                    resultado += "ORIGEN: "+cursor.getString(posColumnaCelularOrigen)+
                                 "\nMENSAJE: "+cursor.getString(posColumnaMensaje)+
                                 "\nFECHA: "+ Date(fechamensaje.toLong()) +
                                 "\n------------------------\n"
                }while(cursor.moveToNext())
            }else{
                resultado = "NO HAY SMS EN BANDEJA DE ENTRADA"
            }
        }
        textView2.setText(resultado)
    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this)
            .setMessage("SE OTORGO RECIBIR")
            .show()

    }

    private fun envioSMS() {
        SmsManager.getDefault().sendTextMessage(editTextPhone.text.toString(), null, editText.text.toString(), null, null)
        Toast.makeText(this, "SE ENVIO EL SMS", Toast.LENGTH_LONG).show()
        editText.text.clear()
        editTextPhone.text.clear()
    }
}

/*
    The message format is passed in the Telephony.Sms.Intents.SMS_RECEIVER_ACTION
    as the format String extra, and will be either "3gpp" for GSM/UMTS/LTE messages
    in 3GPP format or "3gpp2" for CDMA/LTE messages in 3gPP2 format.

    De la version m o anteriores no hay diferencia con los mensajes que llegan
    pero de la version m en adelante si hay diferencia entre si se mando a traves de la version CDMA y la version GSM
 */