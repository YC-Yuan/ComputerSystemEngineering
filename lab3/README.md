# Lab3文档

19302010020 袁逸聪

## PartA

### 汉明距离计算函数

```JAVA
public int hammingDistance(int x, int y) {
    int z = x ^ y;
    int num = 0;
    while (z > 0) {
        z = z & (z - 1);
        num++;
    }
    return num;
}
```

### 计算机系统可靠性

系统越少出错,就越可靠

要保证可靠性,则要能够发现那些难以避免的错误,控制其影响范围,乃至弥补后果(差错检测,差错控制,差错纠正)

汉明校验是一种经典的差错检测方法,在奇偶校验的基础上拓展,不仅能检测错误存在,还能定位错误并复原([汉明码原理](https://blog.csdn.net/qq_19782019/article/details/87452394))

而PartA计算的汉明距离,可以用来衡量码组集合的功效:集合中任意两个编码之间汉明距离的最小值记为d

则这种码组最多可以检测d-1位错误,纠正(d-1)/2位错误(汉明码d为3,故最多检测2位纠正1位误码)

## PartB

要求对可中断的修改事物保证一致性

1. 对单个事务:要求实现Write-ahead-log
   1. 流程
      1. 修改前log新旧值
      2. 执行修改
      3. 确认修改都完成后在log中标记
   2. 保持一致
      1. 如果log新旧值不完整,则修改未开始就中断了,没有一致性问题
      2. 如果log新旧值完整但未标记,说明修改过程中中断了,根据旧值恢复
      3. 如果log已标记,则修改过程一定完成了,没有一致性问题
2. 对多个事物:要求实现Read-capture策略
   1. 流程
      1. 获取变量时检查WaterMark(简写wm),若wm比自身小,则覆盖wm,相当于插队
      2. 若wm比自身大,则abort并重启为新事务,相当于被插队后排到队尾

## 设计

### 核心策略

#### 单个事务:用Log保证原子性

基于本项目的需求,单个事务可以实现得很简单,并且能够处理Log自身不可靠的问题

Log:要把a值改成b值->逐个进行修改->全部完成后Log:commit

如果第一步Log出错,修改必然还没有开始,一致性保证

如果逐个修改时出错,根据第一步Log内容复原或执行完,一致性保证

如果第二部Log出错,则虽然修改已完成,仍认为没有完成,如上,一致性保证

如此便保证了原子性:对10条记录的修改,要么都没做,要么都做了,要么在系统恢复后能达到前两种状态

#### 多个事务

多个事物场景下,需要额外考虑前后原子性:一个事务不应该因为与其他事务同时进行而导致结果不同,它们要么先后进行,要么呈现出如同先后进行一般的结果

所以,最简单的方式就是串行化让他们真的先后进行

然而,串行化可能造成浪费:

1. 事务1需要处理A,事务2需要处理B,不论是否同时进行,结果都不会冲突
2. 事务1需要处理AB,事务2需要处理BC,

于是,可以对应地给出两种优化策略:

1. 事务先对所有需要使用的资源获取锁,再开始操作
2. 事务对正在操作的资源加锁,

### 面向对象





## 对Read-Capture策略的理解

### 性能考量