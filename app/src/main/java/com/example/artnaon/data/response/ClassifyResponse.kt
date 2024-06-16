package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClassifyResponse(

	@field:SerializedName("result")
	val result: ClassifyResult? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ClassifyResult(

	@field:SerializedName("genre")
	val genre: String? = null,

	@field:SerializedName("description")
	val description: String? = null
) : Serializable
