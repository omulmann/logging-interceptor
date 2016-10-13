package com.github.t1.log;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.slf4j.*;
import org.slf4j.impl.StaticMDCBinder;

import static com.github.t1.log.LogLevel.*;
import static org.mockito.Mockito.*;

public abstract class AbstractLoggingInterceptorTests {
    static final Object[] NO_ARGS = new Object[0];

    String captureMessage() {
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> objectCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(log, atLeastOnce()).debug(messageCaptor.capture(), objectCaptor.capture());

        return messageCaptor.getValue();
    }

    private static final String BEANS_XML = "" //
            + "<beans>\n" //
            + "</beans>" //
            ;

    @Deployment
    public static JavaArchive loggingInterceptorDeployment() {
        return ShrinkWrap.create(JavaArchive.class) //
                .addPackage(LoggingInterceptor.class.getPackage()) //
                .addAsManifestResource(new StringAsset(BEANS_XML), "beans.xml") //
                ;
    }

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Before
    @After
    public void resetMdc() {
        StaticMDCBinder.reset();
    }

    @Before
    public void initLogLevelDebug() {
        givenLogLevel(DEBUG);
    }

    @After
    public void clearLogPointCache() {
        LoggingInterceptor.CACHE.clear();
    }

    void givenLogLevel(LogLevel level) {
        givenLogLevel(level, log);
    }

    void givenLogLevel(LogLevel level, Logger log) {
        reset(log);
        switch (level) {
            case _DERIVED_:
                throw new IllegalArgumentException("unsupported log level");
            case ALL:
            case TRACE:
                when(log.isTraceEnabled()).thenReturn(true);
            case DEBUG:
                when(log.isDebugEnabled()).thenReturn(true);
            case INFO:
                when(log.isInfoEnabled()).thenReturn(true);
            case WARN:
                when(log.isWarnEnabled()).thenReturn(true);
            case ERROR:
                when(log.isErrorEnabled()).thenReturn(true);
            case OFF:
                break;
        }
    }
}
