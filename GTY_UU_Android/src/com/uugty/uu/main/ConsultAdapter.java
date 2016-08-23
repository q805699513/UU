package com.uugty.uu.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.entity.ConsultEntity;
import com.uugty.uu.person.PersonCenterActivity;

import java.util.List;

/**
 * Created by HolyGoose on 16/8/20.
 */
class ConsultAdapter extends BaseAdapter {
    private Context context;
    private List<ConsultEntity.Consult> ls;
    private LayoutInflater layoutInflater;

    public ConsultAdapter(Context context, List<ConsultEntity.Consult> ls) {
        super();
        this.context = context;
        this.ls = ls;
        this.layoutInflater=LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return ls.get(position).getUserAvatar();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = layoutInflater.inflate(
                    R.layout.consult_list_item_layout, null);
            holder.baseline = (View) view.findViewById(R.id.base_line);
            holder.consult_person_avatar = (SimpleDraweeView) view
                    .findViewById(R.id.consult_person_avatar);
            holder.consult_person_name = (TextView) view
                    .findViewById(R.id.consult_person_name);
            holder.consult_person_state = (TextView) view
                    .findViewById(R.id.consult_person_state);
            holder.consult_person_distance = (TextView) view
                    .findViewById(R.id.consult_person_distance);
            holder.consult_person_truename = (TextView) view
                    .findViewById(R.id.consult_person_truename);
            holder.consult_person_education = (TextView) view
                    .findViewById(R.id.consult_person_education);
            holder.consult_person_drive = (TextView) view
                    .findViewById(R.id.consult_person_drive);
            holder.consult_person_guide = (TextView) view
                    .findViewById(R.id.consult_person_guide);
            holder.consult_line = (View) view.findViewById(R.id.consult_line);
            holder.consult_up_num = (TextView) view
                    .findViewById(R.id.consult_up_num);
            holder.consult_upstate_img = (ImageView) view
                    .findViewById(R.id.consult_upstate_img);
            holder.consult_tag_lin = (TextView) view
                    .findViewById(R.id.consult_tag_lin);
            holder.simpleImage = (TextView) view.findViewById(R.id.framlayout_tv);
            holder.is_veru = (TextView) view.findViewById(R.id.consult_person_veru);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 头像
        if (null != ls.get(position).getUserAvatar()
                && !ls.get(position).getUserAvatar().equals("")) {
            holder.consult_person_avatar.setImageURI(Uri
                    .parse(APPRestClient.SERVER_IP
                            + ls.get(position)
                            .getUserAvatar()
                            .substring(
                                    0,
                                    ls.get(position).getUserAvatar()
                                            .indexOf("."))
                            + "_ya"
                            + ls.get(position)
                            .getUserAvatar()
                            .substring(
                                    ls.get(position).getUserAvatar()
                                            .indexOf("."))));

        } else {
            holder.consult_person_avatar.setImageURI(Uri.parse("res:///"
                    + R.drawable.no_default_head_img));
        }
        if(position == 0){
            holder.baseline.setVisibility(View.GONE);
        }
        // 灰色阴影
        if (ls.get(position).getIsOnline().equals("1")) {
            holder.simpleImage.setVisibility(View.GONE);
        } else {
            holder.simpleImage.setVisibility(View.VISIBLE);
        }
        //是否会员
        if (ls.get(position).getUserIsPromoter().equals("1")) {
            holder.is_veru.setVisibility(View.VISIBLE);
        } else {
            holder.is_veru.setVisibility(View.GONE);
        }
        // 姓名
        if(!ls.get(position).getUserName().equals("")){
            holder.consult_person_name.setText(ls.get(position).getUserName());
        }else{
            holder.consult_person_name.setText("小u");
        }
        // 实名
        if (ls.get(position).getUserIdValidate().equals("2")) {
            holder.consult_person_truename.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_truename.setVisibility(View.GONE);
        }
        // 导游
        if (ls.get(position).getUserTourValidate().equals("2")) {
            holder.consult_person_guide.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_guide.setVisibility(View.GONE);
        }
        // 驾驶
        if (ls.get(position).getUserCarValidate().equals("2")) {
            holder.consult_person_drive.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_drive.setVisibility(View.GONE);
        }
        // 学历
        if (ls.get(position).getUserCertificateValidate().equals("2")) {
            holder.consult_person_education.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_education.setVisibility(View.GONE);
        }
        holder.consult_person_distance.setVisibility(View.GONE);
        holder.consult_line.setVisibility(View.GONE);
        // 是否在线
        if (ls.get(position).getIsOnline().equals("1")) {
            holder.consult_person_state.setText("在线");
        } else if(ls.get(position).getIsOnline().equals("0")){
            holder.consult_person_state.setText("离线");
            //灰色头像尝试
//			Drawable drawable = holder.consult_person_avatar.getDrawable();
//			drawable.mutate();
//			drawable.clearColorFilter();
//			drawable.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
//			holder.consult_person_avatar.setBackground(drawable);
        }
        // 好评数
        if (ls.get(position).getFavourableCommentNum().equals("0")) {
            holder.consult_up_num.setText("暂无好评");
            holder.consult_upstate_img
                    .setImageResource(R.drawable.consult_no_upstate);
        } else {
            holder.consult_up_num.setText(ls.get(position)
                    .getFavourableCommentNum() + "人好评");
            holder.consult_upstate_img
                    .setImageResource(R.drawable.consult_upstate);
        }
        // 标签
        if (!ls.get(position).getMarkContent().equals("")) {
            String label = ls.get(position).getMarkContent().replace(",", "  ");
            holder.consult_tag_lin.setText(label);
        } else {
            holder.consult_tag_lin.setText("暂无标签");
        }

        holder.consult_person_avatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("detailUserId", ls.get(position)
                        .getUserId());
                intent.setClass(context, PersonCenterActivity.class);
                context.startActivity(intent);
            }
        });
        return view;
    }

    public final static float[] BT_SELECTED =  new   float [] {
            0.308f,  0.609f,  0.082f,  0 ,  0 ,
            0.308f,  0.609f,  0.082f,  0 ,  0 ,
            0.308f,  0.609f,  0.082f,  0 ,  0 ,
            0 ,  0 ,  0 ,  1 ,  0
    };

    static class ViewHolder {
        private SimpleDraweeView consult_person_avatar;
        private TextView consult_person_name, consult_person_distance,
                consult_person_state, consult_person_truename,
                consult_person_education, consult_person_drive,
                consult_person_guide, consult_up_num,simpleImage,is_veru;
        private View consult_line;
        private ImageView consult_upstate_img;
        private TextView consult_tag_lin;
        private View baseline;
    }

}
