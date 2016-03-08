/*
 *    Licensed Materials - Property of IBM
 *    5725-I43 (C) Copyright IBM Corp. 2015. All Rights Reserved.
 *    US Government Users Restricted Rights - Use, duplication or
 *    disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*/

package com.ibm.mfp.sample.phoneuser;

import com.ibm.mfp.adapter.api.ConfigurationAPI;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TwilioAccess {
	/*
	 * For more info on JAX-RS see https://jax-rs-spec.java.net/nonav/2.0-rev-a/apidocs/index.html
	 */
		
	//Define logger (Standard java.util.Logger)
	static Logger logger = Logger.getLogger(TwilioAccess.class.getName());

    /*
     * Twilio specific configuration parameters
     */
    private String twilioSID = null;
    private String twilioToken = null;
    private String twilioFrom = null;

    public TwilioAccess(final String sid, final String token, final String from) {

        logger.info(String.format("Initializing a Twilio access object with sid=[%s], token=[%s], from=[%s]", sid, token, from));

        twilioSID = sid == null ? "" : sid.trim();
        twilioToken = token == null ? "" : token.trim();
        twilioFrom = from == null ? "" : from.trim();

        if (twilioSID.isEmpty() || twilioToken.isEmpty() || twilioFrom.isEmpty()) {
            // Something is wrong
            final String message = String.format("Input error SID[%s] Token[%s] Phone[%s] should not be empty",
                    twilioSID, twilioToken, twilioFrom);
            logger.severe(message);
            throw new IllegalArgumentException(message);
        }

        logger.config(String.format("Twilio is initializing with configuration SID = [%s] Token = [%s] Number = [%s]",
                twilioSID, twilioToken, twilioFrom));

        // Check if we can access twilio using our configured parameters
        try {
            getTwilioRestClient().getAccount();
        } catch(final Throwable t) {
            logger.log(Level.SEVERE, String.format("Twilio client failed to initialize with the provided parameters SID = [%s] Token = [%s] Number = [%s]",
                    twilioSID, twilioToken, twilioFrom),
                    t);
        }

        logger.info("Twilio initialized!");
	}

    public boolean send(final String to, final String body) {
        // Build a filter for the MessageList
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Body", body));
        params.add(new BasicNameValuePair("To", to));
        params.add(new BasicNameValuePair("From", twilioFrom));


        try {
            final MessageFactory messageFactory = getTwilioRestClient().getAccount().getMessageFactory();
            final Message message = messageFactory.create(params);
            System.out.println(message.getSid());
            return true;
        } catch (final Exception e) {
            e.printStackTrace();

            logger.log(Level.SEVERE, "Cannot send SMS using the configured account", e);
        }

        return false;
    }

    private TwilioRestClient getTwilioRestClient() {

        return new TwilioRestClient(twilioSID, twilioToken);
    }

}