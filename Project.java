import java.util.*;
//This equipment class was provided by Phil Maguire
public class equipment {
    public static void main(String args[] ) throws Exception {
        String[][] GPS = new String[1001][2];
        Scanner myscanner = new Scanner(System.in);
        for(int i=0;i<1001;i++){
            GPS[i][0]=""+myscanner.nextDouble();
            GPS[i][1]=""+myscanner.nextDouble();
        }
        myscanner.close();
        Brain mybrain = new Brain(GPS);
        String solution = mybrain.getSolution();
        String[] journey=solution.split(",");
        if(Integer.parseInt(journey[0])!=0||Integer.parseInt(journey[journey.length-1])!=0){
            System.out.println("Solution string is defective: it does not start and end in Maynooth");
            System.exit(0);
        }
        for(int i=0;i<GPS.length;i++){
            boolean check=false;
            for(int j=0;j<journey.length;j++){
                if(i==Integer.parseInt(journey[j])){
                    check=true;
                }    
            }
            if(check==false){
                System.out.println("Solution string is defective: there is no "+i);
                System.exit(0);
            }
        }
        if(journey.length!=GPS.length+1){
            System.out.println("Some locations are repeated, but that's OK if you want");
        }
        double hours=0;
        double mps = 800000/3600;
        double mileage=0;
        for(int i=0;i<journey.length-1;i++){    
            int airport1=Integer.parseInt(journey[i]);
            int airport2=Integer.parseInt(journey[i+1]);
            double distance=getDistance(GPS[airport1][0],GPS[airport1][1],GPS[airport2][0],GPS[airport2][1]);
            mileage+=distance;
            hours=hours+distance/mps/3.6+0.5;
            if(distance<100){
                System.out.println("Solution string is defective: the distance between "+journey[i]+" and "+journey[i+1]+" is only "+(int)distance+" km");
                System.exit(0);
            }
        }
        System.out.println("The total distance travelled by the plane is "+(int)mileage+" km");
        System.out.println("The time spent travelling is "+(int)hours+" hours");
        System.out.println(mybrain.getSolution());
    }
        
    public static double getDistance(String lt1, String ln1, String lt2, String ln2){
        final int R = 6371; // Radius of the earth
        Double lat1 = Double.parseDouble(lt1);
        Double lon1 = Double.parseDouble(ln1);
        Double lat2 = Double.parseDouble(lt2);
        Double lon2 = Double.parseDouble(ln2);
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
        Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        return distance;
    }
    
    public static Double toRad(Double value) {
         return value * Math.PI / 180;
    }

}

class Brain
{
    //This array list will hold the path to which will give us the solution string 
    public static List<String> currentPath = new ArrayList<String>();
    public Brain(String[][] inputs)
    {
        //Initialising all the variable for later use 
        int capacity= 1002, currentpos = 0, count=0 ,pos=0, wanted=0;
        double runningSum=1000000, smallest = 10000;
        boolean checked= false, found=false; 

        //check for holding the boolean of visited(true) and unvisited(false) airports
        boolean check[] = new boolean[capacity];
        
        //setting the first and last location/airport to visited as we know they are both 0
        check[0]=true;
        check[1001] = true;
        //add maynooth/ 0 to the start of the path
        currentPath.add("0");
        //While checked which is a boolean stays false we continue 
	    while(!checked)
		{
		    //reset all the booleans , counters , and comparision variables
		    found=false;count=0;smallest=100000;runningSum=100000;
		    //We check if there exists an unvisited airport
		    for(int i=0;i<check.length;i++)
		    {
		         if(!check[i])
		         {
		            //increase count which will help use 
		            count++;
		         }
		    }
		    //if there isn't an unvisited airport we break, set checked to true
		    if(count==0)
		    {
		        checked=true;
		    }
		    /*We iterate through all coordinate from the currentpos which is 0 at the start and if the position i has a distance between current pos and it more than 100km and it's unvisited and smallest than the smallest i so far we make it the next currentpos if it's unbeaten through the loop*/
		    for(int i=1;i<inputs.length;i++)
		    {    
		        double distance=getDistance(inputs[currentpos][0],inputs[currentpos][1],inputs[i][0],inputs[i][1]);
		        //Distance has to be >= 100km
		        if(distance<100)
		        {
		           continue;
		        }
		        else
		        {
		            //If i isn't visited and it's smallest than the current smallest, it becomes the current smallest
		        	if(!check[i] && distance< smallest)
		            {
		        		smallest = distance;
		        		//take the position of the most smallest i
		                pos=i;
		                //set found to true because there exists a i unvisited greater than 100km
		                found=true;
		            }
		        }
		        //This is for the case we don't find an i that is greater than 100km
		        if(!check[i])
		        {
		            //Wanted index is set to i
		            wanted=i;
		        }
		    }
		    //If we couldn't find an unvisited which is >= 100km
		    if(!found)
		    {
		        //We need to find an medium such that we go to that airport then to the unvisited/ wanted airport
		    	for(int i=0;i<1001;i++)
		        {
		            //We iterate through all inputs and calculate the distance from the input i to the wanted index and from currentpos to the input i
		    		double distance1=getDistance(inputs[currentpos][0],inputs[currentpos][1],inputs[i][0],inputs[i][1]);
		            double distance2=getDistance(inputs[i][0],inputs[i][1],inputs[wanted][0],inputs[wanted][1]);
		            //We add them to get the lowest distance combined
		            double sum= distance1+distance2;
		            //if both the distances are >=100km, we check if the sum of them is less than the running sum(to find the smallest sum of distance, Greedy Neighbour)
		            if(distance1>=100&& distance2>=100)
		            {
		            	if(sum<runningSum)
		                {
		                    runningSum=sum;
		                    pos=i;
		                }
		            }              
		        }  
		     }
		     //Then we set pos to currentpos for the next iteration
		     currentpos=pos;
		     //We have now visited the airport at currentpos
		     check[currentpos]=true;
		     //We add the currentpos to the Path
		     currentPath.add(Integer.toString(currentpos)); 
	   }
	   /*Optimization:Crossovers
	   We iterate over the Path, We are trying to find if two airports are swapped, will result in a shorter distance.
	   We have 4 vertices(i-1,i,i+1,i+2) and 3 edges/distances between them. We need to check if the sum of the distances of the 3 
	   edges of the four vertices (i-1,i,i+1,i+2) is bigger than the swapped verision of the vertices (i-1,i+1,i,i+2) */
	   for(int i=1;i<currentPath.size()-2;i++)
	   {
		   double distanceOriginal=getDistance(inputs[Integer.parseInt(currentPath.get(i-1))][0],inputs[Integer.parseInt(currentPath.get(i-1))][1],inputs[Integer.parseInt(currentPath.get(i))][0],inputs[Integer.parseInt(currentPath.get(i))][1])+getDistance(inputs[Integer.parseInt(currentPath.get(i))][0],inputs[Integer.parseInt(currentPath.get(i))][1],inputs[Integer.parseInt(currentPath.get(i+1))][0],inputs[Integer.parseInt(currentPath.get(i+1))][1])+getDistance(inputs[Integer.parseInt(currentPath.get(i+1))][0],inputs[Integer.parseInt(currentPath.get(i+1))][1],inputs[Integer.parseInt(currentPath.get(i+2))][0],inputs[Integer.parseInt(currentPath.get(i+2))][1]);
		   if(getDistance(inputs[Integer.parseInt(currentPath.get(i-1))][0],inputs[Integer.parseInt(currentPath.get(i-1))][1],inputs[Integer.parseInt(currentPath.get(i+1))][0],inputs[Integer.parseInt(currentPath.get(i+1))][1])<100||getDistance(inputs[Integer.parseInt(currentPath.get(i+1))][0],inputs[Integer.parseInt(currentPath.get(i+1))][1],inputs[Integer.parseInt(currentPath.get(i))][0],inputs[Integer.parseInt(currentPath.get(i))][1])<100||getDistance(inputs[Integer.parseInt(currentPath.get(i))][0],inputs[Integer.parseInt(currentPath.get(i))][1],inputs[Integer.parseInt(currentPath.get(i+2))][0],inputs[Integer.parseInt(currentPath.get(i+2))][1])<100)continue;
		   double distanceCrossover=getDistance(inputs[Integer.parseInt(currentPath.get(i-1))][0],inputs[Integer.parseInt(currentPath.get(i-1))][1],inputs[Integer.parseInt(currentPath.get(i+1))][0],inputs[Integer.parseInt(currentPath.get(i+1))][1])+getDistance(inputs[Integer.parseInt(currentPath.get(i+1))][0],inputs[Integer.parseInt(currentPath.get(i+1))][1],inputs[Integer.parseInt(currentPath.get(i))][0],inputs[Integer.parseInt(currentPath.get(i))][1])+getDistance(inputs[Integer.parseInt(currentPath.get(i))][0],inputs[Integer.parseInt(currentPath.get(i))][1],inputs[Integer.parseInt(currentPath.get(i+2))][0],inputs[Integer.parseInt(currentPath.get(i+2))][1]);
		   if(distanceOriginal>distanceCrossover)
		   {
		       //if the Crossover distance/ the swapped verision is smaller than the original distance we swap both of the indexes
			   Collections.swap(currentPath, i, i+1);
		   }
	   }
   }
    
   //Distance between two coordinates method Provided by Phil Maguire(modified so theres no need for the toRad() method )
   public static double getDistance(String lt1, String ln1, String lt2, String ln2){
        final int R = 6371;
        Double lat1 = Double.parseDouble(lt1);
        Double lon1 = Double.parseDouble(ln1);
        Double lat2 = Double.parseDouble(lt2);
        Double lon2 = Double.parseDouble(ln2);
        Double latDistance = Math.toRadians(lat2-lat1);
        Double lonDistance = Math.toRadians(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        return distance;
    }
  public String getSolution(){
    	//Creating the solution string
        String solution=new String("");
        for(int i=0;i<currentPath.size();i++)
        {
        	//When we are at the last index add a 0 to the end of the String
            if(i==currentPath.size()-1)
            {
                solution=solution + "0";
                
            }
            else
            {
            	//Add the path at position i with a ,
                solution=solution + currentPath.get(i)+",";
            }
        }
        return solution; 
    }        
}