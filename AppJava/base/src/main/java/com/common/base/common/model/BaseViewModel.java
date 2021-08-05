package com.common.base.common.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * 基础viewModel
 */
public class BaseViewModel extends ViewModel {
    /**
     * 状态
     */
    private MutableLiveData<Integer> status;

    public LiveData<Integer> getStatus() {
        if (status == null) {
            status = new MutableLiveData<>();
        }
        return status;
    }
}
