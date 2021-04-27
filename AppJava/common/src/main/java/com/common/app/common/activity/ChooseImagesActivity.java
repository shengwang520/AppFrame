package com.common.app.common.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.common.app.R;
import com.common.app.common.base.BaseActivity;
import com.common.app.common.base.BaseMenuHolder;
import com.common.app.common.base.ToolbarFinder;
import com.common.app.common.dexterpermission.DexterPermissionsUtil;
import com.common.app.common.utils.CropUtils;
import com.common.app.common.utils.ToastUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.orhanobut.logger.Logger;
import com.sheng.wang.media.FileCompat;
import com.sheng.wang.media.impl.CallBack;
import com.sheng.wang.media.model.FileBean;
import com.sheng.wang.media.model.FileFolder;
import com.sheng.wang.media.utils.ImageIntentUtils;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择多图
 */
public class ChooseImagesActivity extends BaseActivity {
    private static final int REQUEST_CAMERA = 0x100;//照相请求码
    private static final String DATA = "choose_data";
    private static final String ISMORE_KEY = "choose_more";
    private static final String CHOOSE_NUM = "choose_num";
    private static final String IS_CROP_KEY = "choose_crop";
    public static int MAX_NUM = 6;//多选的最大数量
    private String takePath;//照相路径
    private ArrayList<FileBean> chooseData;
    private ArrayList<FileBean> results;
    private ChooseImagesHolder holder;
    private BaseMenuHolder menuHolder;
    private ImagesAdapter adapter;
    private List<FileBean> listImages;

    private FileFolder imageFolder;
    private ChooseImageFolderPopWindow popWindow;

    private boolean isMore = true;//默认为多选
    private boolean isCrop = false;//默认为不裁切，仅在单选时有用

    public static Intent newIntent(Context context) {
        return new Intent(context, ChooseImagesActivity.class);
    }

    public static Intent newIntent(Context context, int num) {
        return new Intent(context, ChooseImagesActivity.class).putExtra(CHOOSE_NUM, num);
    }

    public static Intent newIntent(Context context, boolean isMore) {
        return new Intent(context, ChooseImagesActivity.class).putExtra(ISMORE_KEY, isMore);
    }

    public static Intent newIntent(Context context, boolean isMore, boolean isCorp) {
        return new Intent(context, ChooseImagesActivity.class).putExtra(ISMORE_KEY, isMore).putExtra(IS_CROP_KEY, isCorp);
    }

    public static Intent newIntent(Context context, ArrayList<FileBean> choosedData) {
        return new Intent(context, ChooseImagesActivity.class).putParcelableArrayListExtra(DATA, choosedData);
    }

    public static Intent newIntent(Context context, ArrayList<FileBean> choosedData, int num) {
        return new Intent(context, ChooseImagesActivity.class).putParcelableArrayListExtra(DATA, choosedData).putExtra(CHOOSE_NUM, num);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_activity_choose_imgs);
        isMore = getIntent().getBooleanExtra(ISMORE_KEY, true);
        isCrop = getIntent().getBooleanExtra(IS_CROP_KEY, false);
        chooseData = new ArrayList<>();
        listImages = new ArrayList<>();
        results = getIntent().getParcelableArrayListExtra(DATA);
        MAX_NUM = getIntent().getIntExtra(CHOOSE_NUM, 6);

        holder = new ChooseImagesHolder(this);
        adapter = new ImagesAdapter(this);
        holder.recyclerView.setAdapterWithProgress(adapter);

        popWindow = new ChooseImageFolderPopWindow(this);
        popWindow.setOnClickChooseImageListener(new ChooseImageFolderPopWindow.OnClickChooseImageListener() {
            @Override
            public void onChooseImage(FileFolder image) {
                imageFolder = image;
                adapter.clear();
                adapter.add(new FileBean(true));
                adapter.addAll(imageFolder.getImages(chooseData));
                holder.initImageFolder(imageFolder.name);
            }
        });

        holder.bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.show(v);
            }
        });

        loadImages();
    }

    /**
     * 加载图片
     */
    private void loadImages() {
        FileCompat fileCompat = new FileCompat(this, new CallBack() {
            @Override
            public void onSuccess(List<FileFolder> results) {
                popWindow.init(results);
                imageFolder = results.get(0);
                holder.initImageFolder(imageFolder.name);
                initImgs();
            }

            @Override
            public void onError() {

            }
        });
        fileCompat.loadImages();
    }

    /**
     * 初始化图片显示处理
     */
    private void initImgs() {
        listImages.clear();
        listImages.addAll(imageFolder.images);
        List<FileBean> chooses = new ArrayList<>();
        if (results != null && !results.isEmpty()) {
            for (FileBean choose : results) {
                for (FileBean img : listImages) {
                    if (TextUtils.equals(choose.getFilePathQ(), img.getFilePathQ())) {
                        img.isChoose = true;
                        chooses.add(img);
                    }
                }
            }
            chooseData.addAll(chooses);
        }

        adapter.add(new FileBean(true));
        adapter.addAll(listImages);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMore) {
            holder.toolbar.inflateMenu(R.menu.menu_view);
            menuHolder = new BaseMenuHolder(holder.toolbar);
            menuHolder.init(String.format(getString(R.string.confirm_s), String.valueOf(chooseData.size())));
            menuHolder.oneItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (chooseData.isEmpty()) {
                        ToastUtils.show(getActivity(), getString(R.string.least_one_choice));
                    } else {
                        setResult(RESULT_OK, new Intent().putParcelableArrayListExtra(RESULT_DATA, chooseData));
                        finish();
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA://照相返回结果
                    FileBean fileBean = new FileBean(takePath);
                    if (isMore) {//多选逻辑
                        chooseData.add(0, fileBean);
                        setResult(RESULT_OK, new Intent().putParcelableArrayListExtra(RESULT_DATA, chooseData));
                        finish();
                    } else {//单选
                        if (isCrop) {
                            CropUtils.crop(getActivity(), fileBean.getFilePathQ());
                        } else {
                            setResult(RESULT_OK, new Intent().putExtra(RESULT_DATA, fileBean));
                            finish();
                        }
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        String cropPath = resultUri.getPath();
                        if (!TextUtils.isEmpty(cropPath)) {
                            Logger.d("cropPath:" + cropPath);
                            setResult(RESULT_OK, new Intent().putExtra(RESULT_DATA, new FileBean(cropPath)));
                            finish();
                        }
                    }
                    break;
                case UCrop.RESULT_ERROR:
                    break;
            }
        }
    }

    /**
     * 请求拍照权限后拍照
     */
    private void cameraImg() {
        DexterPermissionsUtil.requestPermission(this, Manifest.permission.CAMERA, new DexterPermissionsUtil.CallBack() {
            @Override
            public void showPermissionGranted(String permission) {
                takePath = ImageIntentUtils.takePicture(getActivity(), REQUEST_CAMERA);
            }

            @Override
            public void showPermissionDenied(String permission, boolean permanentlyDenied) {

            }
        });
    }

    class ChooseImagesHolder extends ToolbarFinder {
        private EasyRecyclerView recyclerView;
        private TextView tvName;
        private View bottomView;

        ChooseImagesHolder(Activity activity) {
            super(activity);
            initTab(getTextView(getString(R.string.selector_picture)));
            recyclerView = activity.findViewById(R.id.easyRecyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
            SpaceDecoration itemDecoration = new SpaceDecoration(1);//参数是距离宽度
            recyclerView.addItemDecoration(itemDecoration);

            tvName = findViewById(R.id.tv_name);
            bottomView = findViewById(R.id.ll_bottom_view);
        }

        void initImageFolder(String folderName) {
            tvName.setText(folderName);
            bottomView.setVisibility(View.VISIBLE);
        }
    }

    class ImagesAdapter extends RecyclerArrayAdapter<FileBean> {

        ImagesAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder<FileBean> OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImagesItemHolder(parent, R.layout.x_item_choose_imgs);
        }

    }

    class ImagesItemHolder extends BaseViewHolder<FileBean> {

        private ImageView iv;
        private AppCompatCheckedTextView checkBox;
        private TextView tv_camera;

        ImagesItemHolder(ViewGroup parent, @LayoutRes int res) {
            super(parent, res);
            iv = itemView.findViewById(R.id.iv);
            checkBox = itemView.findViewById(R.id.check);
            tv_camera = itemView.findViewById(R.id.tv_camera);
        }

        @Override
        public void setData(final FileBean data) {
            super.setData(data);
            if (data.isCamera) {//第1个为相机
                checkBox.setVisibility(View.GONE);
                iv.setVisibility(View.GONE);
                tv_camera.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.x_loading_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //点击去拍照
                        if (isMore) {//如果是多选，需要判断是否已经选够
                            if (chooseData.size() == MAX_NUM) {
                                ToastUtils.show(getActivity(), String.format(getString(R.string.choose_more_pictures_s), String.valueOf(MAX_NUM)));
                            } else {
                                cameraImg();
                            }
                        } else {//单选直接拍照
                            cameraImg();
                        }
                    }
                });
            } else {//后面为选图
                tv_camera.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(data.getFilePathQ())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.x_loading_image)
                                .error(R.drawable.x_loading_image))
                        .into(iv);
                checkBox.setChecked(data.isChoose);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMore) {//如果是多选
                            if (data.isChoose) {
                                chooseData.remove(data);
                                data.isChoose = false;
                                checkBox.setChecked(false);
                            } else {
                                if (chooseData.size() == MAX_NUM) {
                                    ToastUtils.show(getActivity(), String.format(getString(R.string.choose_more_pictures_s), String.valueOf(MAX_NUM)));
                                } else {
                                    data.isChoose = true;
                                    chooseData.add(data);
                                    checkBox.setChecked(true);
                                }
                            }

                            menuHolder.init(String.format(getString(R.string.confirm_s), String.valueOf(chooseData.size())));
                        } else {//单选
                            if (isCrop) {
                                CropUtils.crop(getActivity(), data.getFilePathQ());
                            } else {
                                setResult(RESULT_OK, new Intent().putExtra(RESULT_DATA, data));
                                finish();
                            }

                        }
                    }
                });
            }

        }
    }

}
