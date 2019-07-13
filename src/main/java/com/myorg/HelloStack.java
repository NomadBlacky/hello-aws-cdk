package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.TopicProps;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HelloStack extends Stack {
  public HelloStack(final Construct parent, final String id) {
    this(parent, id, null);
  }

  public HelloStack(final Construct parent, final String id, final StackProps props) {
    super(parent, id, props);

    Queue queue = new Queue(this, "MyFirstQueue", QueueProps.builder()
        .withVisibilityTimeout(Duration.seconds(300))
        .build());

    Topic topic = new Topic(this, "MyFirstTopic", TopicProps.builder()
        .withDisplayName("My First Topic Yeah")
        .build());

    topic.addSubscription(new SqsSubscription(queue));

    HelloConstruct hello = new HelloConstruct(this, "Buckets", HelloConstructProps.builder()
        .withBucketCount(5)
        .build());

    User user = new User(this, "MyUser", UserProps.builder().build());
    hello.grantRead(user);

    Map<String, PolicyDocument> inlinePolicies = new HashMap<String, PolicyDocument>() {
      {
        put(
            "ExamplePolicy",
            new PolicyDocument(
                PolicyDocumentProps.builder().withStatements(
                    Collections.singletonList(new PolicyStatement(
                        PolicyStatementProps.builder()
                            .withEffect(Effect.ALLOW)
                            .withActions(Arrays.asList(
                                "cloudformation:Describe*",
                                "cloudformation:List*",
                                "cloudformation:Get*"
                            ))
                            .withResources(Collections.singletonList("*"))
                            .build()
                    ))
                ).build()
            )
        );
      }
    };
    new Role(this, "MyRole", RoleProps.builder().withAssumedBy(user).withRoleName("MyRole").withInlinePolicies(inlinePolicies).build());
  }
}
