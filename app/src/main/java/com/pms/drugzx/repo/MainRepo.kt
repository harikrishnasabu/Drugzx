package com.pms.drugzx.repo

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pms.drugx.api.ApiService
import com.pms.drugzx.api.MyRetrofitBuilder
import com.pms.drugzx.datamodels.Login
import com.pms.drugzx.datamodels.api.CustomerOrder
import com.pms.drugzx.datamodels.api.Products
import com.pms.drugzx.datamodels.api.User
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MainRepository {

    var job: CompletableJob? = null
lateinit var _selectedProducts:List<Products>
     var _customerOrder:MutableLiveData<CustomerOrder> = MutableLiveData()
    fun getProducts(): LiveData<List<Products>>{
        job = Job()
        return object: LiveData<List<Products>>(){
            override fun onActive() {
                super.onActive()
                job?.let{ theJob ->
                    CoroutineScope(IO + theJob).launch {
                        val products = MyRetrofitBuilder.apiService.getProducts()
                        withContext(Main){
                            value = products
                            println("PRODUCT"+value)
                            theJob.complete()
                        }
                    }

                }

            }
        }
    }

    fun searchProducts(search:String): LiveData<List<Products>>{
        job = Job()
        return object: LiveData<List<Products>>(){
            override fun onActive() {
                super.onActive()
                job?.let{ theJob ->
                    CoroutineScope(IO + theJob).launch {
                        val list = MyRetrofitBuilder.apiService.searchProduct(search)
                        withContext(Main){
                            value = list
                            println("SEARCH"+value)
                            theJob.complete()
                        }
                    }

                }

            }
        }
    }

    fun getUser(login: Login):LiveData<User> {

        job = Job()
        return object: LiveData<User>(){
            override fun onActive() {
                super.onActive()
                job?.let{ theJob ->
                    CoroutineScope(IO + theJob).launch {
                        val user = MyRetrofitBuilder.apiService.getUser(ApiService.LoginPostData(login.userName,login.password))
//                        user.enqueue(object: Callback<User> {
//
//
//
//                            override fun onResponse(call: Call<User>, response: Response<User>) {
//                                if(response.isSuccessful){
//                                    println("onResponse"+response.body().toString())
//                                }
//                                else{
//                                   println("Failed to retrieve items")
//                                }
//                            }
//
//                            override fun onFailure(call: Call<User>, t: Throwable) {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                        })
                        withContext(Main){
                            value = user
                          //  println("USER"+value)
                            theJob.complete()
                        }
                    }

                }

            }
        }
    }
    fun sendOrder(customerOrder: CustomerOrder){
        job = Job()
        _customerOrder.value=customerOrder

                job?.let{ theJob ->
                    CoroutineScope(IO + theJob).launch {
                        val addCustomer = MyRetrofitBuilder.apiService.postOrder(_customerOrder.value!!)
                        withContext(Main){

                            println("customerOrder"+addCustomer.toString())
                            theJob.complete()
                        }
                    }


        }
    }

    fun cancelJobs(){
        job?.cancel()
    }



}

