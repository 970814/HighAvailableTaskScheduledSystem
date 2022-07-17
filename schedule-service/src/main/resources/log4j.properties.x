### 设置###
log4j.rootLogger = stdout,E

#%p: 输出日志信息优先级，即DEBUG，INFO，WARN，ERROR，FATAL
#%d: 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy-MM-dd HH:mm:ss,SSS}，SSS为毫秒数(也可以写为SS，只不过SSS如果不足三位会补0)，输出类似：2011-10-18 22:10:28,021
#%r: 输出自应用启动到输出该日志耗费的毫秒数
#%t: 输出产生日志的线程名称
#%l: 输出日志事件的位置，相当于%c.%M(%F:L)的组合，包括类全名、方法、文件名以及在代码中行数。例如:cn.xm.test.PlainTest.main(PlanTest.java:12)
#%c: 输出日志信息所属的类目，通常就是所在类的全名。可写为%c{num},表示取完整类名的层数，从后向前取，比如%c{2}取 "cn.qlq.exam"类为"qlq.exam"。
#%M: 输出产生日志信息的方法名%x: 输出和当前线程相关联的NDC(嵌套诊断环境),尤其用到像java servlets这样的多客户多线程的应用中
#%%: 输出一个"%"字符
#%F: 输出日志消息产生时所在的文件名称
#%L: 输出代码中的行号
#%m: 输出代码中指定的消息,产生的日志具体信息
#%n: 输出一个回车换行符，Windows平台为"\r\n"，Unix平台为"\n"输出日志信息换行

### 输出信息到控制抬 ###
log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n

### 输出DEBUG 级别以上的日志到=E://logs/error.log ###
#log4j.appender.D = org.apache.log4j.ConsoleAppender
#log4j.appender.D.Target = System.out
#log4j.appender.D.layout = org.apache.log4j.PatternLayout
#log4j.appender.D.layout.ConversionPattern = [%-5p]

### 输出ERROR 级别以上的日志到=E://logs/error.log ###
log4j.appender.E = org.apache.log4j.ConsoleAppender
log4j.appender.E.Target = System.out
log4j.appender.E.layout = org.apache.log4j.PatternLayout
log4j.appender.E.layout.ConversionPattern = [%-5p]

#### 输出DEBUG 级别以上的日志到=E://logs/error.log ###
#log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
#log4j.appender.D.File = E://logs/log.log
#log4j.appender.D.Append = true
#log4j.appender.D.Threshold = DEBUG
#log4j.appender.D.layout = org.apache.log4j.PatternLayout
#log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
#
#### 输出ERROR 级别以上的日志到=E://logs/error.log ###
#log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
#log4j.appender.E.File =E://logs/error.log
#log4j.appender.E.Append = true
#log4j.appender.E.Threshold = ERROR
#log4j.appender.E.layout = org.apache.log4j.PatternLayout
#log4j.appender.E.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
#
