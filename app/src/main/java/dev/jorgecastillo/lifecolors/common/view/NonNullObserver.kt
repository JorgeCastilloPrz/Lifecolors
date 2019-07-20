package dev.jorgecastillo.lifecolors.common.view

import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class NonNullObserver<T : Any>(val onChanged: (T) -> Unit) : Observer<T> {
  override fun onChanged(value: T?) {
    value!!.let(onChanged)
  }
}

class NonNullMutableLiveData<T : Any>(initialValue: T) : MutableLiveData<T>() {

  companion object {
    private val ALWAYS_ON = object : LifecycleOwner {

      private val registry = LifecycleRegistry(this).apply {
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        handleLifecycleEvent(Lifecycle.Event.ON_START)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
      }

      override fun getLifecycle(): Lifecycle {
        return registry
      }
    }
  }

  init {
    setValue(initialValue)
  }

  fun observe(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, NonNullObserver(observer))
  }

  fun observe(owner: LifecycleOwner, observer: NonNullObserver<T>) {
    removeObservers(owner)

    super.observe(owner, observer)
  }

  fun observeForever(observer: NonNullObserver<T>) {
    super.observeForever(observer)
  }

  override fun postValue(value: T) {
    super.postValue(value)
  }

  override fun setValue(value: T) {
    super.setValue(value)
  }

  @NonNull
  override fun getValue(): T {
    return super.getValue()!!
  }

  fun update(update: (T) -> T) {
    value = update(value)
  }

  fun applyUpdate(update: T.() -> T) {
    value = update(value)
  }
}
