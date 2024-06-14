package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class EditProfileResponse(

	@field:SerializedName("result")
	val result: EditProfileResult? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class EditProfileResult(

	@field:SerializedName("newPassword")
	val passwordEditProfile: String? = null,

	@field:SerializedName("name")
	val nameEditProfile: String? = null,

	@field:SerializedName("profilePicture")
	val pictureEditProfile: String? = null
)
