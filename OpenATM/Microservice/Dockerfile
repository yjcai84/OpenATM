FROM public.ecr.aws/lambda/java:11
# Copy function code and runtime dependencies from Gradle layout
COPY ExtractTransformFunction/build/classes/java/* ${LAMBDA_TASK_ROOT}/
COPY ExtractTransformFunction/build/dependency/* ${LAMBDA_TASK_ROOT}/lib/
#COPY ExtractTransformFunction/* ${LAMBDA_TASK_ROOT}/
# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "ExtractTransform.App::handleRequest" ]
