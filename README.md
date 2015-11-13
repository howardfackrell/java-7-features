#Java 7 Features
Java 7 has several new language features. This repo has a set of tests to demonstrate the new features.
 
##Diamonds
---
No need to declare the type on both sides of the equal sign any more.  On the right you can leave an empty <>
```
    Map<String, List<String>> favoriteFoods = new HashMap<>();
```

##Grouped Exception Handling
You can catch multiple Exceptions in a single catch {} block
```
    try{
       checkForVowel("Howard");
       fail("Shouldn't get this far without an exception");
    } catch (EException | IException | UException e) {
       e.printStackTrace();
       fail("Didn't expect one of these...");
    } catch (AException | OException e) {
       //handle these different for some reason
       assertTrue(true);
    }
```           

##Number Literals
You can place underscores in big numbers to make it easier to see their scale at a glance
```
    public static final int ONE_MILLION = 1_000_000;
```

You can also use binary literals now
```
    assertEquals(5, 0b101);
    assertEquals(36, 0b0010_0100);
```


##Strings in Switch Statements
It seems natural to be able to do this, but prior to Java 7 you couldn't.  
```
    switch (language) {
        case "Java":
            mantra = "Write once, run anywhere";
            break;
    
        case "Erlang":
            mantra = "Let it crash";
            break;
    
        case "Ruby":
            mantra = "Matz is Nice And So We Are Nice";
            break;
    
        default:
            mantra = "?";
            break;
    }
```


##Try with resource
It's a common pattern to have some resource (connection, file, buffer...) that must be closed in a finally block in order
to prevent resource leaks.  Java 7 provides a more concise way of doing this, as long as the resource implements
the Autocloseable Interface

```
    try (CloseableResource a = factory.getResource();
         CloseableResource b = factory.getResource(); ) {
        // use a and b
    }
    // a and b have now been closed
```


##ForkJoinPool and RecursiveTasks
Similar to the ExecutorService available in Java 6, but makes it easier to deal with large tasks that 
Recursively break down into smaller tasks.  The RecursiveTask is a bit like a Future in Scala.
```
    class PiCalculator extends RecursiveTask<BigDecimal> {
        public compute() { ... }
    }
    
    PiCalculator piCalc = new PiCalculator();
    ForkJoinPool pool = new ForkJoinPool();
    pool.execute(piCalc);
        
    piCalc.get();    
    
```


##NIO and File Watching
This is the largest area of change in Java 7. There are new classes for dealing with io in Java 7. 
Much of what is new provides ways to deal with IO without blocking, which usually requires a different approach to writing your code.
There are ways to make the new File IO classes work along side the older File IO classes if needed.

###Create a Path using Paths.get()
```
    Path path = Paths.get("/opt/myfolder", "myfile");
```

###Create / delete files and directories using the Files class
```
    private static void createDirectory(String directoryName) {
        FileAttribute<Set<PosixFilePermission>> permissions =
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxr-x"));
        try {
            Files.createDirectory(Paths.get(directoryName), permissions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void deleteDirectory(String directoryName) {
        try {
            Files.deleteIfExists(Paths.get(directoryName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
```

###Create a writer to write to a file using the Files class
```
    private void updateFile(OpenOption openOption) {
        try (BufferedWriter writer = Files.newBufferedWriter(path.toAbsolutePath(), openOption)) {
            writer.write(new Date().toString());
        } catch (IOException e) {
            fail("Couldn't write to file");
        }
    }
```

###Watch a folder for changes to files
```
    try {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        watchedDirectory.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    
        WatchKey key = null;
        while (running) {
            try {
                key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    System.out.println(event.context().toString() +  "->" + kind + " @ " + new Date());
                    events.add(kind);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!key.reset()) break;
        }
    }catch (IOException e) {
        e.printStackTrace();
        fail("Couldn't watch the file for changes");
    }
```
