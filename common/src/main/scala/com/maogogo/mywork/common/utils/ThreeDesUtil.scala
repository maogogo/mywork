package com.maogogo.mywork.common.utils

import javax.crypto.spec._
import sun.misc._
import javax.crypto._

object ThreeDesUtil {
  private[this] val key = "MaogogoMaogogoMaogogo"
  private[this] val algorithm = "DESede"
  private[this] val ciphe = "DESede/ECB/PKCS5Padding"

  def encrypt(src: String): String = {

    val dks = new DESedeKeySpec(key.getBytes("UTF-8"))
    val securekey = SecretKeyFactory.getInstance(algorithm).generateSecret(dks)

    val cipher = Cipher.getInstance(ciphe)
    cipher.init(Cipher.ENCRYPT_MODE, securekey)
    val b = cipher.doFinal(src.getBytes)

    new BASE64Encoder().encode(b).replaceAll("\r", "").replaceAll("\n", "")
  }

  def decrypt(src: String): String = {
    val bytesrc = new BASE64Decoder().decodeBuffer(src)
    //--解密的key  
    val dks = new DESedeKeySpec(key.getBytes("UTF-8"))
    val securekey = SecretKeyFactory.getInstance(algorithm).generateSecret(dks)

    //--Chipher对象解密  
    val cipher = Cipher.getInstance(ciphe)
    cipher.init(Cipher.DECRYPT_MODE, securekey)
    new String(cipher.doFinal(bytesrc))
  }
}