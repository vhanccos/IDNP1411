package com.example.bodega.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodega.data.database.entities.Customer
import com.example.bodega.data.database.relations.CustomerWithOrders
import com.example.bodega.data.database.relations.OrderSummary
import com.example.bodega.data.repository.BodegaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            try {
                _operationState.value = OperationState.Loading
                val customer = Customer(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    address = address,
                    phone = phone
                )
                repository.insertCustomer(customer)
                _operationState.value = OperationState.Success("Cliente agregado correctamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al agregar cliente: ${e.message}")
            }
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                _operationState.value = OperationState.Loading
                repository.updateCustomer(customer)
                _operationState.value = OperationState.Success("Cliente actualizado correctamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al actualizar cliente: ${e.message}")
            }
        }
    }


    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                _operationState.value = OperationState.Loading
                repository.deleteCustomer(customer)
                _operationState.value = OperationState.Success("Cliente eliminado correctamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al eliminar cliente: ${e.message}")
            }
        }
    }

    fun getCustomerWithOrders(customerId: Int): Flow<CustomerWithOrders> {
        return repository.getCustomerWithOrders(customerId)
    }

    fun getOrderSummariesForCustomer(customerId: Int): Flow<List<OrderSummary>> {
        return repository.getOrderSummariesForCustomer(customerId)
    }

    fun getCustomerById(customerId: Int): Flow<Customer> {
        return repository.getCustomerById(customerId)
    }

    // Estados para UI feedback
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    sealed class OperationState {
        object Idle : OperationState()
        object Loading : OperationState()
        data class Success(val message: String) : OperationState()
        data class Error(val message: String) : OperationState()
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
