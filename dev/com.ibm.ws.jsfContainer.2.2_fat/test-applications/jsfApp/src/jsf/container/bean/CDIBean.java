package jsf.container.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("cdiBean")
@SessionScoped
public class CDIBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data = ":" + getClass().getSimpleName() + ":";

    @PostConstruct
    public void start() {
        System.out.println("TestBean postConstruct called");
        this.data += ":PostConstructCalled:";
    }

    @PreDestroy
    public void stop() {
        System.out.println("TestBean preDestroy called.");
    }

    public void setData(String newData) {
        this.data += newData;
    }

    public String getData() {
        return this.data;
    }

    public String nextPage() {
        return "TestBean";
    }
}
