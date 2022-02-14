//package io.github.nichetoolkit.socket.configure;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
///**
// * <p>NoticeDatasourceConfigure</p>
// * @author Cyan (snow22314@outlook.com)
// * @version v.1.0
// * @company 郑州高维空间技术有限公司(c) 2021-2022
// * @date created on 17:24 2021/9/9
// */
//@Configuration
//@EnableTransactionManagement
//public class SocketDatasourceConfigure {
//    @Primary
//    @Bean(name = "socketDatasource")
//    @ConfigurationProperties(prefix = "spring.datasource.hikari")
//    public HikariDataSource dataSource() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("socketDatasource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:xml/*/*.xml"));
//        return bean.getObject();
//    }
//
//    @Bean(name = "transactionManager")
//    public DataSourceTransactionManager transactionManager(@Qualifier("socketDatasource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean(name = "sqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}
