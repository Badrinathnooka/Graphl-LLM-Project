StarWars GraphQL
Setup Guide (Spring Boot + Ollama + MongoDB Atlas)
This guide walks you through creating a local StarWars knowledge assistant.
The system combines:
● Spring Boot for the backend and GraphQL API
● MongoDB Atlas for a cloud database
● Ollama to run a local large language model (LLM) that converts natural-language
text into GraphQL queries
● A static HTML or Thymeleaf UI served directly by Spring Boot
The end result is an app where you can type a request like “Add character Luke Skywalker
height 172 mass 77”, and the LLM produces the proper GraphQL mutation that inserts a
document into MongoDB.
1. Prerequisites
Before starting, ensure you have:
● Java Development Kit (JDK 17) - Required by Spring Boot 3.5.x
● Spring Tool Suite (STS) / IntelliJ IDEA - Any Java IDE with Maven support
● Maven 3.9+ - Dependency management
● MongoDB Atlas Cluster - Cloud NoSQL database
● Ollama 0.3.1 - Local LLM runtime
2. Project Overview
● The StarWars Assistant is a Java-based web app that merges several tools into one
seamless system.
● Spring Boot powers the backend, providing a stable environment for running the
application.
● With Spring Boot GraphQL, it offers a flexible API to query or update StarWars
data like characters and species.
● Persistent storage is handled by MongoDB Atlas, a cloud database that scales easily
and needs no local setup.
● A local LLM from Ollama interprets natural-language requests and converts them
into proper GraphQL queries.
● For the interface, the app serves static HTML so users can interact through any
browser without extra frontend code.
3.Import into IDE
Unzip your project into a working folder:
● Open Spring Tool Suite (STS)
● Import as Maven Project
● Verify Maven builds successfully (mvn clean install)
4. MongoDB Atlas Setup
1. Go to MongoDB Atlas
2. Create a Free Cluster
3. Create Database: starwar
○ Collections: characters, species
Get Connection String (URI) like:
mongodb+srv://<user>:<password>@cluster0.xxxxx.mongodb.net/starwar
4. Update src/main/resources/application.properties:
spring.application.name=AssignmentProject
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.xxxxx.
mongodb.net/starwar
spring.data.mongodb.database=starwar
server.port=8080
5. Ollama Setup
5.1 Install Ollama
● Windows: Download installer from https://ollama.ai/download
macOS:
brew install ollama/tap/ollama
Linux:
curl -fsSL https://ollama.ai/install.sh | sh
5.2 Verify
Ollama is the local runtime that will host the large-language model used to convert plain English into
GraphQL queries.
Follow these sub-steps carefully to get it working on your machine.
5.1 Install Ollama
Choose the command that matches your operating system:
● Windows – Download the installer directly from
https://ollama.ai/download and run it like a normal setup file.
macOS – Open Terminal and run: brew install ollama/tap/ollama
Linux – Use the one-line shell script: curl -fsSL https://ollama.ai/install.sh | sh
This places the ollama binary in your system path so it can be called from any terminal.
5.2 Confirm Installation
After installation, open a new terminal or command prompt and check the version:
ollama - - version
Seeing a version number means Ollama is ready.
5.3 Download a Model
Ollama itself is just the runtime; you still need a model.
For a lightweight option (~2.5 GB) pull the llama3:1 model: ollama pull llama3:1
5.4 Run the Model
Start the model to accept prompts: ollama run llama3:1
This keeps the model active and listening on its default API port (11434).
No GPU? No Problem
If your computer doesn’t have a GPU, you can force CPU-only mode:
Windows PowerShell
set OLLAMA_NO_GPU=1
macOS / Linux
export OLLAMA_NO_GPU=1
Running in CPU mode is slower but ensures compatibility on any machine.
6. Spring Boot Backend
6.1 Start Backend
Run:
mvn spring-boot: run
API will be available at: http://localhost:8080/graphql
6.2 Example GraphQL Query
query {
 listCharacters {
 id
 name
 gender
 }
}
7. Frontend (Static HTML in Spring Boot)
● Place HTML files in src/main/resources/static/
● Access via browser: http://localhost:8080/
8. Ollama + Spring Boot Integration
In LlmService.java you can connect to Ollama via REST call or subprocess.
Example REST (WebClient):
WebClient webClient = WebClient.builder()
 .baseUrl("http://localhost:11434") // Ollama default port
 .build();
public String queryOllama(String prompt) {
 return webClient.post()
 .uri("/api/generate")
 .bodyValue(Map.of("model", "llama3:1", "prompt", prompt))
 .retrieve()
 .bodyToMono(String.class)
 .block();
}
9. Example Workflow
1. User inputs: “add character Luke Skywalker height=172 mass=77”
2. Ollama converts → GraphQL Mutation
3. Spring Boot GraphQL executes mutation → MongoDB Atlas insert
4. Response returns as JSON
