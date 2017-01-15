package redisTest.model;

/**
 * Created by zhangrui25 on 2017/1/15.
 */
public class Article {
    private Long articleId ;
    private String title ;
    private String link ;
    private Long time ;
    private Integer votes ;
    private String poster ;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
