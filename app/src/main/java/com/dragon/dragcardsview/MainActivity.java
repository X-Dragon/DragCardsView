package com.dragon.dragcardsview;

import java.util.ArrayList;

import com.dragon.dragcardsview.DragCardsView.onDragListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private DragCardsView mDragCardsView;
	private CardsAdapter mCardAdapter;
	private ArrayList<Integer> cardList = new ArrayList<Integer>();
	private int[] imgRes=new int[]{
			R.drawable.pic_1,
			R.drawable.pic_2,
			R.drawable.pic_3,
			R.drawable.pic_4,
			R.drawable.pic_5,
			R.drawable.pic_6,
			R.drawable.pic_7,
			R.drawable.pic_8,
			R.drawable.pic_9,
			R.drawable.pic_10
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDragCardsView = (DragCardsView) findViewById(R.id.dragCardsView);
		findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDragCardsView != null) {
					mDragCardsView.rotationLeft();
				}
			}

		});

		findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDragCardsView != null) {
					mDragCardsView.rotationtRight();
				}
			}

		});
		for (int i = 0; i < imgRes.length; i++) {
			cardList.add(imgRes[i]);
		}
		mCardAdapter = new CardsAdapter();
		mDragCardsView.setAdapter(mCardAdapter);
		mDragCardsView.setFlingListener(new onDragListener() {

			@Override
			public void removeFirstObjectInAdapter(boolean isLeft) {
				// TODO Auto-generated method stub
				if (isLeft) {
					Toast.makeText(MainActivity.this, "向左划了一张牌",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(MainActivity.this, "向右划了一张牌",
							Toast.LENGTH_SHORT).show();
				}
				if (cardList.size() > 0) {
					cardList.remove(0);
				}
				mCardAdapter.notifyDataSetChanged();
			}

			@Override
			public void onSelectLeft(double distance) {
				// TODO Auto-generated method stub
//				if (mDragCardsView != null) {
//					View firstCard = mDragCardsView.getFirstCard();
//					if (firstCard != null) {
//						ImageView iv_like = (ImageView) firstCard
//								.findViewById(R.id.iv_like);
//						ImageView iv_dislike = (ImageView) firstCard
//								.findViewById(R.id.iv_dislike);
//						iv_dislike.setVisibility(View.VISIBLE);
//						iv_like.setVisibility(View.INVISIBLE);
//
//					}
//				}
			}

			
			@Override
			public void onSelectRight(double distance) {
				// TODO Auto-generated method stub
//				if (mDragCardsView != null) {
//					View firstCard = mDragCardsView.getFirstCard();
//					if (firstCard != null) {
//						ImageView iv_like = (ImageView) firstCard
//								.findViewById(R.id.iv_like);
//						ImageView iv_dislike = (ImageView) firstCard
//								.findViewById(R.id.iv_dislike);
//						iv_like.setVisibility(View.VISIBLE);
//						iv_dislike.setVisibility(View.INVISIBLE);
//					}
//				}

			}

			
			@Override
			public void onCardReturn() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onCardMoveDistance(double distance) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAdapterAboutToEmpty(int itemsInAdapter) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "需要补牌了", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class CardsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cardList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cardList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View cardView = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.layout_card, parent, false);
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), cardList.get(position));
			MyImageView iv_image = (MyImageView) cardView
					.findViewById(R.id.iv_image);
			iv_image.setImageDrawable(new HalfRoundedDrawable(bitmap, 15, 0));
			return cardView;
		}

	}

}
