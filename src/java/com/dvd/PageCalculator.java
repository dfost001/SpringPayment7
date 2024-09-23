
/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import error_util.EhrLogger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import util.BeanUtil;

/**
 *
 * @author Dinah
 */
@Component
@Scope("session")
public class PageCalculator implements Serializable{
    private Integer pageSize;
    private Integer noIntervals;
    private Integer recordCount;
    private Integer maxPageNo; //the greatest number that will show in the page table,i.e.,50 for 1000 records
    private Integer currentEnd; //record index end
    private Integer currentStart; //record index start
    private Integer pgNoStart; //number display start
    private Integer pgNoEnd ; //number display end
    private Integer pgSelected = 1; //re-initialized by calculateFromPageNo after invoking BeanUtils
    private static final Integer defaultPageSize = 20;
    private static final Integer defaultNoIntervals = 10;   
    
    
   
    public void initialize(Integer recordCount) {
        
        this.recordCount = recordCount;
        
        pageSize = defaultPageSize;
        
        noIntervals=defaultNoIntervals;
        
        this.calcMaxPageNo();
        
        this.pgNoEnd = noIntervals > maxPageNo ? maxPageNo : noIntervals;
        
        this.pgNoStart = 1;
       
        this.calculateRecordSetIndices(pgNoStart);       
    }
    
      private void calcMaxPageNo(){
         this.maxPageNo = this.recordCount/this.pageSize;
         if(maxPageNo == 0)
             maxPageNo = 1;
         else if(recordCount%pageSize > 0){
           maxPageNo++;
          }
     }  

    public Integer getRecordCount() {
        return recordCount;
    }   
    
    public Integer getPageSize(){
        return this.pageSize;
    }
    
    public Integer getNoIntervals() {
        return this.noIntervals;
    }

    public Integer getCurrentEnd() {
        return currentEnd;
    }
    /*
     * Returns a 1-based record number
     */
    public Integer getCurrentStart() {
        return currentStart;
    }

    public Integer getPgNoStart() {
        return pgNoStart;
    }

    public Integer getPgNoEnd() {
        return pgNoEnd;
    }

    public Integer getMaxPageNo() {
        return maxPageNo;
    } 

    public Integer getPgSelected() {
        return pgSelected;
    }      
    
    public void calcNext(){
        
        BeanUtil.evalNullOrEmptyFields(this.getClass(), this);
        
        if(this.pgNoEnd.equals(this.maxPageNo))
                return; //URL requested from browser cache/list
        
        this.pgNoStart = this.pgNoEnd + 1;
        
        this.pgNoEnd = pgNoStart + noIntervals - 1;
        
        pgNoEnd = pgNoEnd > maxPageNo ? maxPageNo : pgNoEnd;
        
        this.calculateRecordSetIndices(pgNoStart);
       
    }
    
    public void calcPrevious() {
        
        BeanUtil.evalNullOrEmptyFields(this.getClass(), this);
        
        if(this.pgNoStart == 1)
            return;
        
        this.pgNoStart = this.pgNoStart - this.noIntervals;
        
        this.pgNoEnd = pgNoStart + noIntervals - 1;
        
        this.calculateRecordSetIndices(pgNoStart);
       
    } 
   
    /*
     * Called from initialize() to set initial values for currentStart and 
     * currentEnd so other fields used in calculation have to be specified.
     *
     * To do: Custom Exception that resolves to Browser navigation error
     * if record index requested does not exist in underlying (gt max)
     * since this may be a history request and record count has changed. 
     */
    public void calcFromPageNo(int pageNo){
        
         BeanUtil.throwFieldsNotInitialized(this.getClass(), this,
                  "pageSize", "recordCount", "noIntervals", "maxPageNo");       
       
        if(pageNo > maxPageNo) //recordcount may have changed since a history request or render error
             EhrLogger.throwIllegalArg(
                    this.getClass().getCanonicalName(), 
                    "calcFromPageNo", 
                    "Requested page-number is greater than max-page-no. History request or render error. ");
             
        if(pageNo < 1)
            EhrLogger.throwIllegalArg(
                    this.getClass().getCanonicalName(), 
                    "calcFromPageNo", 
                    "Calculation of record index requires a positive value. " +
                            "Check the EL render.");
        
        calcPageNoEndpoints(pageNo); //reset endpoints of pagination table for a book-marked request
        
        calculateRecordSetIndices(pageNo);
        
    }  
    /*
     * Start at begin set to 1, and calculate endpoints until interval containing
     * the page-no parameter is found
     */
    private void calcPageNoEndpoints(int selectedPgNo) {
        
        /* BeanUtil.throwFieldsNotInitialized(this.getClass(), this,
                  "pageSize", "recordCount", "noIntervals", "maxPageNo"); */
        
        int begin = 1;
        
        while(begin < this.maxPageNo) {
            
            int end = begin + this.noIntervals - 1;
            
            end = end > maxPageNo ? maxPageNo : end;
            
            if(selectedPgNo >= begin && selectedPgNo <= end) {
                
                this.pgNoStart = begin;                   
                
                pgNoEnd = end ;
                
                break;
            }
            
            begin = end + 1;
        }
    }   
     
    private void calculateRecordSetIndices(int pageNo) {
        currentEnd = pageNo * pageSize <= recordCount ? pageNo * pageSize : recordCount;
        
        currentStart = (pageNo - 1) * this.pageSize + 1; //simpler calculation if the record-count is not even    
       
        this.pgSelected = pageNo; //Alters the display
    }
    
} // end class
