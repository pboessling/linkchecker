package de.phib.linkchecker;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Link> reader() {
        return new FlatFileItemReaderBuilder<Link>()
                .name("linkItemReader")
                .resource(new ClassPathResource("input.csv"))
                .delimited()
                .names(new String[]{"url"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Link>() {{
                    setTargetType(Link.class);
                }})
                .build();
    }

    @Bean
    public FlatFileItemWriter<Link> writer() {
        return new FlatFileItemWriterBuilder<Link>()
                .name("linkItemWriter")
                .resource(new FileSystemResource("output.csv"))
                .lineAggregator(new DelimitedLineAggregator<Link>() {{
                    setFieldExtractor(new BeanWrapperFieldExtractor<Link>() {{
                        setDelimiter(";");
                        setNames(new String[]{"url", "status"});
                    }});
                }})
                .build();
    }

    @Bean
    public LinkItemProcessor processor() {
        return new LinkItemProcessor();
    }

    @Bean
    public Job checkLinksJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("checkLinksJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(FlatFileItemWriter<Link> writer) {
        return stepBuilderFactory.get("step1")
                .<Link, Link>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}
