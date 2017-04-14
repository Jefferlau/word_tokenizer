使用 ANSJ 对搜狗实验室全网新闻数据进行分词，再用 word2vec 对关键词聚类
---

# 准备

## word2vec
[word2vec](https://github.com/svn2github/word2vec)

- Run 'make' to compile word2vec tool
- Run the demo scripts: ./demo-word.sh and ./demo-phrases.sh
- For questions about the toolkit, see http://groups.google.com/group/word2vec-toolkit

下载并编译 word2vec
```bash
git clone https://github.com/svn2github/word2vec.git
cd word2vec
make
```

## 项目编译

```
mvn package -DskipTests
```
得到 target/ansj-word-package.jar 可执行文件，后续说明如何使用。

# 分词聚类

## 下载新闻数据并解压
访问 [搜狗实验室新闻数据](http://www.sogou.com/labs/resource/ftp.php?dir=/Data/SogouCA/news_tensite_xml.full.tar.gz)申请账号密码，然后下载此文件。
```bash
tar -zxf news_tensite_xml.full.tar.gz
```
## 提取内容
去除 <content> 标签以外的内容
```bash
cat news_tensite_xml.dat | iconv -f gbk -t utf-8 -c | grep "<content>" > corpus.txt
```
## 分词
利用本项目对上一步处理过的文件进行分词，词语间以空格分割。
```bash
nohup ./ansj-word-package.jar corpus.txt resultbig.txt > logs/out.log &
```
## 分析
对上一步生成的结果文件 resultbig.txt 进行分析，生成 vectors.bin文件，便于以后重复利用。
```bash
nohup word2vec/word2vec -train resultbig.txt -output vectors.bin -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12 -binary 1 &
```
## 计算接近的词或短语测试
由于 word2vec 计算的是余弦值，距离范围为 0-1 之间，值越大代表这两个词关联度越高，所以越排在上面的词与输入的词越紧密。
```bash
word2vec/distance vectors.bin
```
然后按照提示输入词汇或短语，查看与之相近的词汇。

## 聚类及排序
```bash
word2vec/word2vec -train resultbig.txt -output classes.txt -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12 -classes 500
sort classes.txt -k 2 -n > classes.sorted.txt
```

# 参考资料

- [ANSJ](https://github.com/NLPchina/ansj_seg)
- [搜狗实验室全网新闻](http://www.sogou.com/labs/resource/ca.php)
- [利用Ansj进行新闻关键词提取](http://blog.csdn.net/zhaoxinfan/article/details/10403917)
- [利用word2vec对关键词进行聚类](http://blog.csdn.net/zhaoxinfan/article/details/11069485)
- [ANSJ调用word2vec model文件](http://blog.csdn.net/zhaoxinfan/article/details/11640573)
- [利用中文数据跑Google开源项目word2vec](http://www.cnblogs.com/hebin/p/3507609.html)

# 扩展资料

- [机器学习-斯坦福大学](https://www.coursera.org/learn/machine-learning/home/info)
- [文本数据的机器学习自动分类方法](http://www.infoq.com/cn/articles/machine-learning-automatic-classification-of-text-data)
- [IKAnalyzer中文分词，计算句子相似度](https://my.oschina.net/twosnail/blog/370744)
- [Spark MLlib实现的中文文本分类–Naive Bayes](http://lxw1234.com/archives/2016/01/605.htm)
- [How to train word2vec and GloVe word vectors: 中文文本处理简要介绍](http://sentiment-mining.blogspot.com/2016/01/how-to-train-word2vec-and-glove-word.html)
- [中文分词－HMM模型](https://maples.me/algorithm/2016/03/30/Chinese-word-Segment-HMM/)
- [word2vec 原理](http://blog.csdn.net/zhaoxinfan/article/details/27352659)
- [DL4J](https://deeplearning4j.org/cn/quickstart)
- [DL4J word2vec](https://deeplearning4j.org/cn/word2vec)