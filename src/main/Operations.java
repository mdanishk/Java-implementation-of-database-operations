package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.out;

public class Operations implements Serializable{
	
	public static void main(String[] args) throws FileNotFoundException {

		
		var test = new TupleGeneratorImpl ();

    /*    test.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id",
                           null);*/
        
        test.addRelSchema ("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id",
                           null);
        
        test.addRelSchema ("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode",
                           null);
        
        test.addRelSchema ("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crsCode semester",
                           new String [][] {{ "profId", "Professor", "id" },
                                            { "crsCode", "Course", "crsCode" }});
   /*     
        test.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester",
                           new String [][] {{ "studId", "Student", "id"},
                                            { "crsCode", "Course", "crsCode" },
                                            { "crsCode semester", "Teaching", "crsCode semester" }});*/

        List<Long> joinTime=new ArrayList<Long>();
        List<Long> selectPointTime=new ArrayList<Long>();
        List<Long> selectRangeTime=new ArrayList<Long>();
        Long totalJoinTime=0L;
        Long totalSelectPointTime=0L;
        Long totalSelectrangeTime=0L;
        Long avgTime=0L;
        /**
         * for number of tuples ranging from 1000-8000
         * 
         */
        
        var tups = new int[] {100000}; 
        
        for(int s = 0; s< tups.length ; s++){

        int tup [] = new int [] {10000,1,5000};
        out.println ();
        out.println("number of tuples - " +tups[s]);
        var resultTest = test.generate (tup);
        
        /**
         * Professor table;
         */
        Table prof = new Table("Professor", "id name deptId", "Integer String String", "id");
        
        /**
         * Teaching table
         */
        Table teach = new Table("Teaching","crsCode semester profId","String String String String","crsCode semester");

			/**
			 * 
			 *  Inserting tuples in Professor Table
			 */
        
            for (int j = 0; j < resultTest [0].length; j++) {
            	prof.insert(resultTest[0][j]);				
            } // for
      
            /**
             * 
             *  Inserting tuples in Teaching Table
             */
        
            for (int j = 0; j < resultTest [2].length; j++) {
            	teach.insert(resultTest[2][j]);
            } // for
        
            Comparable keyval_for_select=resultTest[0][1][0];
            String column="id";
        
            out.println ();
        for(int i = 0; i <=10 ; i ++) {
            
            //index join
        	 Long start = System.currentTimeMillis();
        	 var i_join  = prof.i_join("id","profId",teach);         
        	 //i_join.print();
        	 Long end = System.currentTimeMillis();
        	 Long time_elapsed = end - start;
        	 joinTime.add(time_elapsed);
        	 System.out.println("time_elapsed in ms for indexed join"+time_elapsed);
           
            
            
            System.out.println("=================Select Operation=============================");
            
            //select-point
            start=System.currentTimeMillis();
            prof.select (new KeyType (keyval_for_select));
            end=System.currentTimeMillis();
            time_elapsed = end - start;
            selectPointTime.add(time_elapsed);
            System.out.println("time_elapsed in ms for select-point query"+time_elapsed);
            
          //select-range
            start=System.currentTimeMillis();
            var t_select2 = prof.select (t -> (Integer) t[prof.col(column)] > 1);
            end=System.currentTimeMillis();
            time_elapsed = end - start;
            selectRangeTime.add(time_elapsed);
            System.out.println("time_elapsed in ms for select-range query"+time_elapsed);
            
        }
        
        }//for
        
        for(int i=1;i<selectPointTime.size();i++) {
        	totalJoinTime=totalJoinTime+joinTime.get(i);
        	totalSelectPointTime=totalSelectPointTime+selectPointTime.get(i);
        	totalSelectrangeTime=totalSelectrangeTime+selectRangeTime.get(i);
        	
        	
        }
        
        System.out.println("Join average" +totalJoinTime/selectPointTime.size() );
        System.out.println("Select point average" +totalSelectPointTime/selectPointTime.size() );
        System.out.println("Select Range average" +totalSelectrangeTime/selectPointTime.size() );        
	
	}

}
