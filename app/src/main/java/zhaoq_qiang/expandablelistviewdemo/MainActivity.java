package zhaoq_qiang.expandablelistviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * 可伸缩listViewDemo,仿qq好友效果：
 *
 * 使用自定义expandableListView
 */
public class MainActivity extends AppCompatActivity {

    private MyExpandableListView listView;
    private String[][] childrenData = new String[10][10];
    private String[] groupData = new String[10];

    private int expandFlag = -1; //控制列表的展开
    private PinnedHeaderExpandableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (MyExpandableListView) findViewById(R.id.expand_list_view);

        initData();
    }

    /**
     * 初始化数据：
     */
    private void initData() {

        for (int i = 0; i < 10; i++) {
            groupData[i] = "分组" + i;
        }

        for (int i = 0; i < 10; i++) {
            for(int j = 0;j<10;j++){
                childrenData[i][j] = "好友" + i + "-"+j;
            }
        }

        //设置悬浮头部VIEW:
        listView.setHeaderView(getLayoutInflater().inflate(
                R.layout.group_head,listView,false
        ));

        adapter = new PinnedHeaderExpandableAdapter(childrenData, groupData, getApplicationContext(),listView);
        listView.setAdapter(adapter);
    }

    class GroupClickListener implements ExpandableListView.OnGroupClickListener {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {
            if (expandFlag == -1) {
                // 展开被选的group
                listView.expandGroup(groupPosition);
                // 设置被选中的group置于顶端
                listView.setSelectedGroup(groupPosition);
                expandFlag = groupPosition;
            } else if (expandFlag == groupPosition) {
                listView.collapseGroup(expandFlag);
                expandFlag = -1;
            } else {
                listView.collapseGroup(expandFlag);
                // 展开被选的group
                listView.expandGroup(groupPosition);
                // 设置被选中的group置于顶端
                listView.setSelectedGroup(groupPosition);
                expandFlag = groupPosition;
            }
            return true;
        }
    }
}

