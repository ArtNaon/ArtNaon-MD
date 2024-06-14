package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("result")
	val result: UserResult? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UserResult(

	@field:SerializedName("result")
	val result: List<String?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("picture")
	val picture: String? = null,

	@field:SerializedName("paintings")
	val paintings: List<String>? = null // List of painting URLs

)
