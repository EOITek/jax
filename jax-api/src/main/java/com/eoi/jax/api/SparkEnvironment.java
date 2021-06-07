/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.api;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

/**
 * spark runtime
 */
public class SparkEnvironment {
    public final SparkContext sparkContext;
    public final SparkSession sparkSession;
    public final SparkConf sparkConf;
    public final JavaSparkContext javaSparkContext;
    public final SQLContext sqlContext;

    public SparkContext getSparkContext() {
        return sparkContext;
    }

    public SparkSession getSparkSession() {
        return sparkSession;
    }

    public SparkConf getSparkConf() {
        return sparkConf;
    }

    public JavaSparkContext getJavaSparkContext() {
        return javaSparkContext;
    }

    public SQLContext getSqlContext() {
        return sqlContext;
    }

    public SparkEnvironment(SparkContext sparkContext, SparkSession sparkSession) {
        this(sparkContext,sparkSession, sparkContext.getConf());
    }

    public SparkEnvironment(SparkContext sparkContext, SparkSession sparkSession, SparkConf sparkConf) {
        this.sparkContext = sparkContext;
        this.sparkSession = sparkSession;
        this.sparkConf = sparkConf;
        this.javaSparkContext = new JavaSparkContext(this.sparkContext);
        this.sqlContext = sparkSession.sqlContext();
    }
}
