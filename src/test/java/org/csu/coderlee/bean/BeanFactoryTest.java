package org.csu.coderlee.bean;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

/**
 * Created by lixiang on 16/3/22.
 */

//下面这句话,表示不检测过期的方法
@SuppressWarnings("deprecation")
public class BeanFactoryTest {

    @Test
    public void testSimpleLoad(){

        //读取配置文件beanFactory
        BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("spring/beanFactoryTest.xml"));

        //根据beanFactoryTest.xml中的配置找到对应的类的配置,并且实例化
        MyTestBean bean = (MyTestBean) beanFactory.getBean("myTestBean");

        assertEquals("mytestmessage", bean.getTestStr());
    }
}
