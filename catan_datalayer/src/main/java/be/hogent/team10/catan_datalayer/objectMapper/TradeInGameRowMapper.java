/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_datalayer.objectMapper;

import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.model.TradeRequest;
import be.hogent.team10.catan_businesslogic.model.TradeRequestStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author HP
 */
public class TradeInGameRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        TradeRequest tradeRequest = new TradeRequest();
        for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
            
            String colname = rs.getMetaData().getColumnName(col);
            if (colname.equalsIgnoreCase("TRADE_ID")) {
                tradeRequest.setTradeRequestId(rs.getInt("TRADE_ID"));
            } else if (colname.equalsIgnoreCase("SENDING_PLAYER_ID")) {
                tradeRequest.setPlayerSending(rs.getInt("SENDING_PLAYER_ID"));
            } else if (colname.equalsIgnoreCase("RECIEVING_PLAYER_ID")) {
                tradeRequest.setPlayerReceiving(rs.getInt("RECIEVING_PLAYER_ID"));
            } else if (colname.equalsIgnoreCase("STATUS")) {
                tradeRequest.setStatus(Enum.valueOf(TradeRequestStatus.class, rs.getString("STATUS")));
            }
            tradeRequest.setOffer((ResourceSet)(new ResourceSetInGameRowMapper("S_").mapRow(rs, i)));
            tradeRequest.setReward((ResourceSet)(new ResourceSetInGameRowMapper("R_").mapRow(rs, i)));
            
        }
        return tradeRequest;
    }
}
