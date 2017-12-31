package com.dianbin.latte.ec.main.cart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.dianbin.latte.app.Latte;
import com.dianbin.latte.delegates.bottom.BottomItemDelegate;
import com.dianbin.latte.ec.R;
import com.dianbin.latte.ec.R2;
import com.dianbin.latte.net.RestClient;
import com.dianbin.latte.net.callBack.ISuccess;
import com.dianbin.latte.ui.recycle.MultipleItemEntity;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhouyixin on 2017/12/31.
 */

public class ShopCartDelegate extends BottomItemDelegate implements ISuccess {

    private ShopCartAdapter mAdapter = null;

    //TODO 底层的封装再看一遍，尤其是ButtonKnife的封装
    @BindView(R2.id.rv_shop_cart)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.icon_shop_cart_select_all)
    IconTextView mIconSelectAll = null;

    /**
     * 这部分和视频里的不一样，视频里的代码虽然效率稍微高一点（不需要for循环），但是它容易出错。
     * 视频中是在Adapter中设个是否被全选的参数，通过public方法传入，在convert方法里置入IS_SELECTED
     * 这样如果当其它地方有重新调用adapter的notify系列的方法，会出错！即假如这个“是否被全选的参数”
     * 为false，并且已有一个被选了，此时如果其它地方有调用adapter的notify系列的方法，会出错！
     */
    @OnClick(R2.id.icon_shop_cart_select_all)
    void onClickSelectAll() {
        final List<MultipleItemEntity> data = mAdapter.getData();
        final int tag = (int) mIconSelectAll.getTag();
        if (tag == 0) {
            for (MultipleItemEntity entity : data) {
                entity.setField(ShopCartItemFields.IS_SELECTED, true);
            }
            mIconSelectAll.setTextColor(ContextCompat.getColor(Latte.getApplicationContext(), R.color.app_main));
            mIconSelectAll.setTag(1);
        } else {
            for (MultipleItemEntity entity : data) {
                entity.setField(ShopCartItemFields.IS_SELECTED, false);
            }
            mIconSelectAll.setTextColor(Color.GRAY);
            mIconSelectAll.setTag(0);
        }
        mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
    }

    /**
     * 原视频上的方法是错的，优化了群上的解决方法，更易懂，并且效率更高
     */
    @OnClick(R2.id.tv_top_shop_cart_remove_selected)
    void onClickRemoveSelectedItem() {
        final List<MultipleItemEntity> data = mAdapter.getData();
        //要删除的数据
        final List<MultipleItemEntity> deleteEntities = new ArrayList<>();
        for (MultipleItemEntity entity : data) {
            final boolean isSelected = entity.getField(ShopCartItemFields.IS_SELECTED);
            if (isSelected) {
                deleteEntities.add(entity);
            }
        }

        //从List中最后一个开始删除，防止引起下标的变动
        for (int i = deleteEntities.size() - 1; i >= 0; i--) {
            int removePosition = deleteEntities.get(i).getField(ShopCartItemFields.POSITION);
            mAdapter.remove(removePosition);
        }

        //不用再取一遍，因为list只是引用，只有一个对象
        final int size = data.size();
        for (int j = 0; j < size; j++) {
            data.get(j).setField(ShopCartItemFields.POSITION, j);
        }
    }

    @OnClick(R2.id.tv_top_shop_cart_clear)
    void onClickClear(){
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_shop_cart;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mIconSelectAll.setTag(0);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        RestClient.builder()
                .url("shop_cart.php")
                .loader(getContext())
                .success(this)
                .build()
                .get();

    }

    @Override
    public void onSuccess(String response) {
        final ArrayList<MultipleItemEntity> data = new ShopCartDataConverter().setJsonData(response).convert();
        mAdapter = new ShopCartAdapter(data);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
