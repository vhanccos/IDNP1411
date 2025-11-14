package com.example.bodega.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.relations.CustomerWithOrders
import com.example.bodega.data.repository.BodegaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: BodegaRepository) : ViewModel() {

    val allCustomers: StateFlow<List<Customer>> = repository.getAllCustomers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCustomer(firstName: String, lastName: String, email: String, address: String, phone: String) {
        viewModelScope.launch {
            val customer = Customer(
                firstName = firstName,
                lastName = lastName,
                email = email,
                address = address,
                phone = phone
            )
            repository.insertCustomer(customer)
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun getCustomerWithOrders(customerId: Int): Flow<CustomerWithOrders> {
        return repository.getCustomerWithOrders(customerId)
    }

    fun getCustomerById(customerId: Int): Flow<Customer> {
        return repository.getCustomerById(customerId)
    }
}
