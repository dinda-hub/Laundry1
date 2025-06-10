package com.reza.laundry.modeldata

import java.io.Serializable

class modeltransaksitambahan (
    var id: String = "",
    var idLayanan: String? = "",
    var namaLayanan: String? = "",
    var hargaLayanan: String? = "",
    val jamDibuat: String? = null,
    val tanggalTerdaftar: String? = ""
) : Serializable