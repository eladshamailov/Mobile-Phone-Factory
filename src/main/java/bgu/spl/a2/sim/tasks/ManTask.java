package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

    public class ManTask extends Task<Product> {

        private Product product;
        private Warehouse warehouse;

        public ManTask(Product product, Warehouse warehouse) {
            this.product = product;
            this.warehouse = warehouse;
        }

        /**
         * start for manufactoringPlan class
         */
        protected void start()
        {
            ManufactoringPlan plan = warehouse.getPlan(product.getName());

            if (plan.getParts().length > 0) {

                List<ManTask> manufacturedTasks = manfactureParts(plan.getParts());

                whenResolved(manufacturedTasks, () -> {
                            long allValues = FinalIdOfTools(plan.getTools());
                            product.setFinalId(product.getStartId() + allValues);
                            complete(product);
                        }
                );
            }
            else
            {
                product.setFinalId(product.getStartId());
                complete(product);
            }
        }
        /**
         * build and return a list of manufacture Task
         * @param plans
         * @return manufacturingTasks
         */
        private List<ManTask> manfactureParts(String[] plans)
        {
            List<ManTask> tasks = new LinkedList<>();
            for(int i=0;i<plans.length;i++){
                Product part = new Product(product.getStartId()+1, plans[i]);
                product.addPart(part);
                ManTask manufacturingTask = new ManTask(part, warehouse);
                spawn(manufacturingTask);
                tasks.add(manufacturingTask);
            }
            return tasks;
        }

        /**
         * returns the calculated finalId
         * @param tools
         * @return finalId
         */
        private long FinalIdOfTools(String[] tools)
        {
            long finalId = 0;
            List<Long> toolResults = new LinkedList<>();
            CountDownLatch countDownLatch = new CountDownLatch(tools.length);

            for (String toolType : tools)
            {
                Deferred<Tool> toolDeferred = warehouse.acquireTool(toolType);

                toolDeferred.whenResolved(() -> {
                    toolResults.add(toolDeferred.get().useOn(product));
                    warehouse.releaseTool(toolDeferred.get());
                    countDownLatch.countDown();
                });
            }

            try
            {
                countDownLatch.await();
            }
            catch (InterruptedException exception) {}

            for(int i=0;i<toolResults.size();i++){
                if(toolResults.get(i)!=null)
                    finalId=finalId+toolResults.get(i);
            }
            return finalId;
        }
    }