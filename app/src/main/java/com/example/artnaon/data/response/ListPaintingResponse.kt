package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class ListPaintingResponse(

	@field:SerializedName("paintings")
	val paintings: List<String?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
