package project.jaehyeok.ggym;

//public class GroupListViewHolder extends RecyclerView.ViewHolder {
//    TextView textText;
//
//    public GroupListViewHolder(@NonNull View itemView) {
//        super(itemView);
//
//        textText = itemView.findViewById(R.id.testText);
//
//        textText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onViewHolderItemClickListener.onViewHolderItemClick();
//            }
//        });
//    }
//
//    public void onBind() {
//
//    }
//
//    private void changeVisibility(final boolean isExpanded) {
//        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
//        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, 600) : ValueAnimator.ofInt(600, 0);
//        // Animation이 실행되는 시간, n/1000초
//        va.setDuration(500);
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                // imageView의 높이 변경
//                textText.getLayoutParams().height = (int) animation.getAnimatedValue();
//                textText.requestLayout();
//                // imageView가 실제로 사라지게하는 부분
//                textText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//            }
//        });
//        // Animation start
//        va.start();
//    }
//
//    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
//        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
//    }
//}
