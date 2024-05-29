package com.example.lawyerapplication.utils

import android.util.Log

open class ValidPassword() // основной конструктор
{
    fun String.isMixedCase() = any(Char::isLowerCase) && any(Char::isUpperCase)
    fun String.hasSpecialChar() = any { it in "!,+^" }
    fun checkPasswd(newPassword: String, newPasswordRepeat: String): Boolean {
        return !(newPassword != newPasswordRepeat || newPassword.length < 8 || newPassword.count(Char::isDigit) < 0 || !newPassword.isMixedCase() || !newPassword.hasSpecialChar())
    }

    fun checkPassword(oldPassword: String, newPassword: String, newPasswordRepeat: String): String {
        for (index in newPassword.indices) {
            if(newPassword[index].toString() == " ") {
                return "Пароль не может содержать пробелы"
            }
        }

        return if(newPassword != newPasswordRepeat) {
            "Введенные пароли не совпадают"
        } else if (newPassword.length < 8) {
            "Пароль должен быть не менее 8 символов"
        } else if (!(newPassword.count(Char::isDigit) > 0)) {
            "Пароль не содержит не одной цифры"
        } else if (!newPassword.isMixedCase()) {
            "Пароль должен содержать большие и малые латинские символы"
        } else if (!newPassword.hasSpecialChar()) {
            "Пароль не содержит не одного специального знака"
        } else if(oldPassword == newPassword) {
            "старый пароль совпадает с новым"
        } else {
            "ok"
        }

    }

/*fun String.isLongEnough() = length >= 8
fun String.hasEnoughDigits() = count(Char::isDigit) > 0
fun String.isMixedCase() = any(Char::isLowerCase) && any(Char::isUpperCase)
fun String.hasSpecialChar() = any { it in "!,+^" }
*/

}
