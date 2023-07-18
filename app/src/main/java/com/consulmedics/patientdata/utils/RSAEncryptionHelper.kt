package com.consulmedics.patientdata.utils
import android.util.Base64
import android.util.Log
import java.lang.Exception
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 *
 * RSA is one of the first public-key cryptosystems and is widely used for secure data transmission.
 * In such a cryptosystem, the encryption key is public and distinct from the decryption key which is kept secret.
 *
 * This class helps to you for in RSA encryption and decryption operation.
 *
 * @author Cafer Mert Ceyhan
 * **/
object RSAEncryptionHelper {

    fun encryptStringWithPrivateKey(plainText: String, privateKeyStr: String): String {
        try{
            // Convert private key string to PrivateKey object
            val privateKey = convertPrivateKey(privateKeyStr)

            // Initialize the cipher with the private key and encryption mode
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, privateKey)

            // Encrypt the plain text
            return Base64.encodeToString(cipher.doFinal(plainText.toByteArray()), Base64.DEFAULT)
        }
        catch (e: Exception){
            e.printStackTrace()
            return ""
        }

//        return cipher.doFinal(plainText.toByteArray()).toString()
    }

    fun convertPrivateKey(privateKeyStr: String): PrivateKey {
        // Remove the "BEGIN PRIVATE KEY" and "END PRIVATE KEY" headers and any line breaks
        val privateKeyPEM = privateKeyStr.replace("-----BEGIN PRIVATE KEY-----\n", "")
            .replace("-----END PRIVATE KEY-----", "").replace("\n", "")

        // Decode the base64-encoded private key
        val privateKeyBytes = android.util.Base64.decode(privateKeyPEM, android.util.Base64.DEFAULT)

        // Generate the PKCS8EncodedKeySpec from the decoded bytes
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)

        // Get the RSA KeyFactory and generate the PrivateKey
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
}