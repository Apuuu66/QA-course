package com.guier.solr;

import com.guier.pojo.Product;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Guier on 2019/3/18.
 */
public class SolrManager {
    /**
     * solr服务的基地址
     */
    private static final String BASE_URL = "http://localhost:9999/solr/course";

    /**
     * @throws
     * @Description: solr7已经删除了HttpSolrClient的构造方法创建SolrClient, 需要使用Builder来构建
     * @param: @return
     * @return: HttpSolrClient
     */
    public static HttpSolrClient getSolrClient() {
        return new HttpSolrClient.Builder(BASE_URL)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }


    @Test
    public void addIndex() throws IOException, SolrServerException {
        SolrClient solrClient = getSolrClient();

        //添加时可以不指定id，solr会默认使用自己添加id例如：0be216be-de68-43b5-8917-a01e59936ce4
        //如果指定了id，在solr记录中存在就会更新信息，并且是真的更新，id还是原来那个
        SolrInputDocument document = new SolrInputDocument();
        //document.addField("id", "0be216be-de68-43b5-8917-a01e59936ce4");
        document.addField("product_name", "牛皮糖");

        solrClient.add(document);
        solrClient.commit();
    }

    @Test
    public void delIndex() throws IOException, SolrServerException {

        SolrClient solrClient = getSolrClient();

        //根据id删除
        //solrClient.deleteById("0be216be-de68-43b5-8917-a01e59936ce4");

        //根据条件删除
        solrClient.deleteByQuery("product_name:牛皮");

        solrClient.commit();
    }

    /**
     * @throws
     * @Description: 使用   MapSolrParams 查询
     * @param: @throws SolrServerException
     * @param: @throws IOException
     * @return: void
     */
    @Test
    public void testMapSolrParams() throws SolrServerException, IOException {
        SolrClient solrClient = getSolrClient();

        Map<String, String> map = new HashMap<>();
        //指定查询关键词,只推荐写一个q，如果还有其他条件使用过滤条件
//        map.put("q", "product_name:黑色");
        map.put("q", "*:*");

        //fl:指定需要查询的域名称，则默认查询全部域
        //map.put("fl", "id, product_name,product_price");


        //指定排序：asc升序, desc降序
        //map.put("sort", "product_price asc");


        /*
         * 使用过滤条件查询:
         * 例如下面:查询价格小于100为[* TO 100],TO必须要是大写,
         * 查询价格大于100的[100 TO *],[]表示包含，不包含使用{}
         * 例如{],表示前包后不包
         */
        //map.put("fq", "product_price:[* TO 100]");
        //map.put("fq", "product_price:[100 TO *]");
        //map.put("fq", "product_shop_name:旗舰店");

        //使用rows指定查询多少行，还可以指定start表示从查询结果的第几条开始选取指定条数，就是分页
        //map.put("rows", "1");

        /*
         * 指定默认搜索域
         * 如果搜索时没有指定搜索域则默认搜索这里指定的域
         */
        map.put("df", "product_name");

        SolrParams solrParams = new MapSolrParams(map);
        QueryResponse queryResponse = solrClient.query(solrParams);
        System.out.println(queryResponse);
    }

    /**
     * @throws
     * @Description: 使用 SolrQuery 查询
     * @param: @throws SolrServerException
     * @param: @throws IOException
     * @return: void
     */
    @Test
    public void testSolrQuery() throws SolrServerException, IOException {
        SolrClient solrClient = getSolrClient();
        //关键词 过滤条件  排序 分页 开始行 每页数 高亮 默认域 只查询指定域
        SolrQuery solrQuery = new SolrQuery();
        //设置关键词,指定了默认域可以直接写关键词，等同于solrQuery.setQuery("product_name:衣服")
        solrQuery.set("q", "衣服");

        //设置过滤条件
        solrQuery.set("fq", "product_price:[* TO 100]");

        //添加排序条件
        solrQuery.addSort("product_price", SolrQuery.ORDER.desc);

        //分页
        solrQuery.setStart(5);
        solrQuery.setRows(10);

        //设置默认域
        solrQuery.set("df", "product_name");

        //查询指定域,与上面一样key为fl,值为需要查询的域名称，这里省略不写了

        //高亮,打开高亮开关,设置高亮前缀和后缀即可使用高亮，此功能solr自带，但是高亮的结果和查询结果不再一个容器需要单独获取
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<color style='color:red'>");
        solrQuery.setHighlightSimplePost("</color>");

        //执行查询
        QueryResponse queryResponse = solrClient.query(solrQuery);

        //获取高亮结果，最外层Map的K为id,V为Map,第二层Map,K为域名称，V为list
        Map<String, Map<String, List<String>>> hightLightMap = queryResponse.getHighlighting();

        //文档结果集
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //总条数
        Long totalCount = solrDocumentList.getNumFound();
        System.out.println("totalCount:" + totalCount);
        for (SolrDocument document : solrDocumentList) {
            //System.out.println(document);
            //获取高亮文档
            Map<String, List<String>> fieldMap = hightLightMap.get(document.get("id"));
            //高亮的内容是搜索的域这里是默认域product_name,且获得的List只有product_name的值
            List<String> productNameList = fieldMap.get("product_name");
            System.out.println(productNameList.get(0));

        }
    }

    @Test
    public void f178() throws IOException, SolrServerException {
        SolrClient solrClient = getSolrClient();
        //关键词 过滤条件  排序 分页 开始行 每页数 高亮 默认域 只查询指定域
        SolrQuery solrQuery = new SolrQuery();
        //设置默认域
        solrQuery.set("df", "product_name");
        solrQuery.set("q", "衣服");


        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<color style='color:red'>");
        solrQuery.setHighlightSimplePost("</color>");


        QueryResponse res = solrClient.query(solrQuery);
        SolrDocumentList results = res.getResults();
        System.out.println(results);

        Map<String, Map<String, List<String>>> highlighting = res.getHighlighting();
        System.out.println(highlighting);
        List<Product> resBeans = res.getBeans(Product.class);
        System.out.println(resBeans);
    }

    public <T> List<T> getBeans(QueryResponse res, Class<T> type) {
        return null;
    }
}
