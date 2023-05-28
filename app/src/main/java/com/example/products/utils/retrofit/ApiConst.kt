package com.example.products.utils.retrofit

const val BASE_URL = "http://192.168.107.43:5218/" /*"http://192.168.1.31:5218/"*/
const val BASE_API_URL = BASE_URL + "api/"

const val AUTH_HEADER = "Authorization"

const val AUTH_TYPE = "Bearer "

const val ACCOUNT_PATH = "account"
const val LOGIN_PATH = "$ACCOUNT_PATH/login"
const val SIGN_IN_PATH = "$ACCOUNT_PATH/signin"
const val CHECK_LOGIN_PATH = "$ACCOUNT_PATH/IsUserExists"
const val GET_PERSON_PATH = "$ACCOUNT_PATH/Person"

const val PRODUCTS_PATH = "products"
const val ORDERS_PATH = "orders"

const val CART_PATH = "cart"
const val ORDER_STATE_PATH = "orderstates"
