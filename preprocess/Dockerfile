FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
VOLUME /tmp
ARG JAR_FILE
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
CMD ["h"]
#for run the preprocess diffrent functionality update the CMD with on of the follow
#also replace the tags [] with correct values
#read file and run queries in that file :                                   f [FileName] [directory for save results] [endpoint with ?stream= or sparql?query= part] ['C' for cumulative result(both Lite and Complete version), 'CL' just lite version, 'CC' just Complete version , 'I' for individual] [folder for save temp files] [number of line to start] [optional : file to check if the same path in this file has result then run query for the path in provided file , just useable for file not for folder]
#read file and  generate all combination for predicates by length [len] :   pc [collected_predicates.json] [len] [pathToSaveResult] [PathToSaveSerialization] [true or false for save the result]
#read the predicate path and generate all queries :                         gq [predicate] [domain] [range] [predicate combination files] [pathToSaveResults] [collected_predicates.json]
