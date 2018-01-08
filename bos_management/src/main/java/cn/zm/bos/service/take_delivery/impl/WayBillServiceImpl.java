package cn.zm.bos.service.take_delivery.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import cn.zm.bos.dao.take_delivery.WayBillDao;
import cn.zm.bos.domain.take_delivery.WayBill;
import cn.zm.bos.index.WayBillIndex;
import cn.zm.bos.service.take_delivery.WayBillService;

@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {

    @Autowired
    private WayBillDao wayBillDao;

    @Autowired
    private WayBillIndex wayBillIndex;

    @Override
    public void save(WayBill wayBill) {
        // 判断运单号是否存在
        WayBill persistWayBill = wayBillDao.findByWayBillNum(wayBill
                .getWayBillNum());
        if (persistWayBill == null || persistWayBill.getId() == null) {
            // 运单不存在
            wayBill.setSignStatus(1);//待发货
            wayBillDao.save(wayBill);
            // 保存索引
            wayBillIndex.save(wayBill);
        } else {
            // 运单存在
            try {
                Integer id = persistWayBill.getId();
                BeanUtils.copyProperties(persistWayBill, wayBill);
                persistWayBill.setId(id);
                persistWayBill.setSignStatus(1);
                // 保存索引
                wayBillIndex.save(persistWayBill);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    @Override
    public Page<WayBill> findPageData(WayBill wayBill, Pageable pageable) {
        // 判断wayBill 条件是否存在
        if (StringUtils.isBlank(wayBill.getWayBillNum())
                && StringUtils.isBlank(wayBill.getSendAddress())
                && StringUtils.isBlank(wayBill.getRecAddress())
                && StringUtils.isBlank(wayBill.getSendProNum())
                && (wayBill.getSignStatus() == null || wayBill.getSignStatus() == 0)) {
            return wayBillDao.findAll(pageable);
        } else {
            // 查询条件
            // must 条件必须成立 and
            // must no 条件必须不成立 not
            // should 条件成立 or
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            // 向组合查询对象添加条件
            if (StringUtils.isNoneBlank(wayBill.getWayBillNum())) {
                // 运单号查询
                QueryBuilder termQuery = new TermQueryBuilder("wayBillNum",
                        wayBill.getWayBillNum());
                boolQueryBuilder.must(termQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendAddress())) {
                // 发货地模糊查询
                //情况一：输入"北"是查询词条一部分，使用模糊匹配词条查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder(
                        "sendAddress", "*" + wayBill.getSendAddress() + "*");
                //情况二：输入"陕西省西安市"是多个词条组合，进行分词后，每个词条匹配查询
                QueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendAddress()).field("sendAddress").defaultOperator(Operator.AND);
                //两种情况取or关系
                BoolQueryBuilder boolBuilder = new BoolQueryBuilder();
                boolBuilder.should(wildcardQuery);
                boolBuilder.should(queryStringQueryBuilder);
                boolQueryBuilder.must(wildcardQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getRecAddress())) {
                // 收货地模糊查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder(
                        "recAddress", "*" + wayBill.getRecAddress() + "*");
                boolQueryBuilder.must(wildcardQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendProNum())) {
                // 速运类型 等值查询
                QueryBuilder termQuery = new TermQueryBuilder("sendProNum",
                        wayBill.getSendProNum());
                boolQueryBuilder.must(termQuery);
            }
            if (wayBill.getSignStatus() != null && wayBill.getSignStatus() != 0) {
                // 签收状态查询
                QueryBuilder termQuery = new TermQueryBuilder("signStatus",
                        wayBill.getSignStatus());
                boolQueryBuilder.must(termQuery);
            }

            SearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
            searchQuery.setPageable(pageable);// 分页效果
            // 有条件查询，查询索引库
            return wayBillIndex.search(searchQuery);
        }
    }

    @Override
    public WayBill findByWayBillNum(String wayBillNum) {
        return wayBillDao.findByWayBillNum(wayBillNum);
    }

    @Override
    public void syncIndex() {
        //查询数据
        List<WayBill> wayBills = wayBillDao.findAll();
        //同步索引库
        wayBillIndex.save(wayBills);
    }

    @Override
    public List<WayBill> findWayBills(WayBill wayBill) {

        // 判断wayBill 条件是否存在
        if (StringUtils.isBlank(wayBill.getWayBillNum())
                && StringUtils.isBlank(wayBill.getSendAddress())
                && StringUtils.isBlank(wayBill.getRecAddress())
                && StringUtils.isBlank(wayBill.getSendProNum())
                && (wayBill.getSignStatus() == null || wayBill.getSignStatus() == 0)) {
            //无条件查询、查询数据库
            return wayBillDao.findAll();
        } else {
            // 查询条件
            // must 条件必须成立 and
            // must no 条件必须不成立 not
            // should 条件成立 or
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            // 向组合查询对象添加条件
            if (StringUtils.isNoneBlank(wayBill.getWayBillNum())) {
                // 运单号查询
                QueryBuilder termQuery = new TermQueryBuilder("wayBillNum",
                        wayBill.getWayBillNum());
                boolQueryBuilder.must(termQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendAddress())) {
                // 发货地模糊查询
                //情况一：输入"北"是查询词条一部分，使用模糊匹配词条查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder(
                        "sendAddress", "*" + wayBill.getSendAddress() + "*");
                //情况二：输入"陕西省西安市"是多个词条组合，进行分词后，每个词条匹配查询
                QueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendAddress()).field("sendAddress").defaultOperator(Operator.AND);
                //两种情况取or关系
                BoolQueryBuilder boolBuilder = new BoolQueryBuilder();
                boolBuilder.should(wildcardQuery);
                boolBuilder.should(queryStringQueryBuilder);
                boolQueryBuilder.must(wildcardQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getRecAddress())) {
                // 收货地模糊查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder(
                        "recAddress", "*" + wayBill.getRecAddress() + "*");
                boolQueryBuilder.must(wildcardQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendProNum())) {
                // 速运类型 等值查询
                QueryBuilder termQuery = new TermQueryBuilder("sendProNum",
                        wayBill.getSendProNum());
                boolQueryBuilder.must(termQuery);
            }
            if (wayBill.getSignStatus() != null && wayBill.getSignStatus() != 0) {
                // 签收状态查询
                QueryBuilder termQuery = new TermQueryBuilder("signStatus",
                        wayBill.getSignStatus());
                boolQueryBuilder.must(termQuery);
            }

            SearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
            Pageable pageable = new PageRequest(0, 10000);
            searchQuery.setPageable(pageable);// 分页效果
            // 有条件查询，查询索引库
            return wayBillIndex.search(searchQuery).getContent();
        }
    }

}
