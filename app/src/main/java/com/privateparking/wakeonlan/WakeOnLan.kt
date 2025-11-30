package com.privateparking.wakeonlan

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun sendWakeOnLan(macAddress: String, ipAddress: String, port: Int = 9) {
    val macBytes = getMacBytes(macAddress)
    val magicPacket = ByteArray(6 + 16 * macBytes.size)

    for (i in 0..5) {
        magicPacket[i] = 0xFF.toByte()
    }

    for (i in 1..16) {
        System.arraycopy(macBytes, 0, magicPacket, i * macBytes.size, macBytes.size)
    }

    val address = InetAddress.getByName(ipAddress)
    val packet = DatagramPacket(magicPacket, magicPacket.size, address, port)
    val socket = DatagramSocket()
    println("Sending WoL packet")
    socket.send(packet)
    socket.close()
}

private fun getMacBytes(macStr: String): ByteArray {
    val bytes = ByteArray(6)
    val hex = macStr.split("[:\\-]".toRegex()).toTypedArray()
    if (hex.size != 6) {
        throw IllegalArgumentException("Invalid MAC address.")
    }
    try {
        for (i in 0..5) {
            bytes[i] = hex[i].toInt(16).toByte()
        }
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("Invalid hex digit in MAC address.")
    }
    return bytes
}
